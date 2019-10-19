/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Albert Moky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ==============================================================================
 */
package chat.dim.mkm;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.*;

import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
import chat.dim.crypto.impl.PublicKeyImpl;
import chat.dim.format.Base64;
import chat.dim.format.JSON;

public class Profile extends TAI {

    /**
     *  Entity name
     */
    private String name;

    /**
     *  Public key (used for encryption, can be same with meta.key)
     *
     *      RSA
     */
    private PublicKey key;

    /**
     *  Called by 'getInstance()' to create Profile
     *
     *  @param dictionary - profile info
     */
    public Profile(Map<String, Object> dictionary) {
        super(dictionary);

        // public key
        key = null;
    }

    /**
     *  Create profile with data and signature
     *
     * @param identifier - entity ID
     * @param data - profile data in JsON format
     * @param signature - signature of profile data
     */
    public Profile(ID identifier, String data, byte[] signature) {
        super(identifier, data, signature);

        // public key
        key = null;
    }

    /**
     *  Create a new empty profile
     *
     * @param identifier - entity ID
     */
    public Profile(ID identifier) {
        this(identifier, null, null);
    }

    @Override
    public boolean verify(PublicKey publicKey) {
        if (!super.verify(publicKey)) {
            return false;
        }

        // get name
        name = (String) getData("name");

        // get public key
        try {
            key = PublicKeyImpl.getInstance(getData("key"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            key = null;
        }
        return true;
    }

    //---- properties getter/setter

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
        setData("name", name);
    }

    public PublicKey getKey() {
        return key;
    }

    public void setKey(PublicKey publicKey) {
        key = publicKey;
        setData("key", key);
    }

    //-------- Runtime --------

    private static List<Class> profileClasses = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static void register(Class clazz) {
        // check whether clazz is subclass of Profile
        assert Profile.class.isAssignableFrom(clazz); // asSubclass
        if (!profileClasses.contains(clazz)) {
            profileClasses.add(0, clazz);
        }
    }

    @SuppressWarnings("unchecked")
    public static Profile getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Profile) {
            return (Profile) object;
        }
        assert object instanceof Map;
        Map<String, Object> dictionary = (Map<String, Object>) object;
        // try each subclass to parse profile
        Constructor constructor;
        for (Class clazz: profileClasses) {
            try {
                constructor = clazz.getConstructor(Map.class);
                return (Profile) constructor.newInstance(dictionary);
            } catch (Exception e) {
                // profile data error, try next
                //e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("unknown profile: " + dictionary);
    }

    static {
        register(Profile.class); // default
        // ...
    }
}

/**
 *  The Additional Information
 *
 *      'Meta' is the information for entity which never changed, which contains the key for verify signature;
 *      'TAI' is the variable part, which contains the key for asymmetric encryption.
 */
abstract class TAI extends Dictionary {

    public final ID identifier;

    private Map<String, Object> properties;
    private String data;      // JsON.encode(properties)
    private byte[] signature; // LocalUser(identifier).sign(data)
    private boolean valid;  // true on signature matched

    TAI(Map<String, Object> dictionary) {
        super(dictionary);
        // ID
        identifier = ID.getInstance(dictionary.get("ID"));

        // properties
        properties = new HashMap<>();
        // data = JsON.encode(properties)
        data = (String) dictionary.get("data");
        // signature = LocalUser(identifier).sign(data)
        String base64 = (String) dictionary.get("signature");
        signature = (base64 == null) ? null : Base64.decode(base64);
        // verify flag
        valid = false;
    }

    TAI(ID identifier, String data, byte[] signature) {
        super();
        // ID
        this.identifier = identifier;
        this.dictionary.put("ID", identifier);

        // properties
        this.properties = new HashMap<>();
        // json data
        this.data = data;
        if (data != null) {
            this.dictionary.put("data", data);
        }
        // signature
        this.signature = signature;
        if (signature != null) {
            this.dictionary.put("signature", Base64.encode(signature));
        }
        // verify flag
        this.valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    /**
     *  Update profile property with key and data
     *  (this will reset 'data' and 'signature')
     *
     * @param key - property key
     * @param value - property data
     */
    public void setData(String key, Object value) {
        // 1. update data in properties
        if (value == null) {
            properties.remove(key);
        } else {
            properties.put(key, value);
        }

        // 2. reset data signature after properties changed
        dictionary.remove("data");
        dictionary.remove("signature");
        data = null;
        signature = null;
        valid = false;
    }

    /**
     *  Get profile property data with key
     *
     * @param key - property key
     * @return property data
     */
    public Object getData(String key) {
        if (!valid) {
            return null;
        }
        return properties.get(key);
    }

    /**
     *  Get all keys for properties
     *
     * @return profile properties key set
     */
    public Set<String> dataKeys() {
        if (!valid) {
            return null;
        }
        return properties.keySet();
    }

    /**
     *  Verify 'data' and 'signature', if OK, refresh properties from 'data'
     *
     * @param publicKey - public key in meta.key
     * @return true on signature matched
     */
    @SuppressWarnings("unchecked")
    public boolean verify(PublicKey publicKey) {
        if (valid) {
            // already verified
            return true;
        }
        if (data == null || signature == null) {
            // data error
            return false;
        }
        if (publicKey.verify(data.getBytes(Charset.forName("UTF-8")), signature)) {
            valid = true;
            // refresh properties
            properties.clear();
            Map<String, Object> dictionary = (Map<String, Object>) JSON.decode(data);
            properties.putAll(dictionary);
        } else {
            data = null;
            signature = null;
        }
        return valid;
    }

    /**
     *  Encode properties to 'data' and sign it to 'signature'
     *
     * @param privateKey - private key match meta.key
     * @return signature
     */
    public byte[] sign(PrivateKey privateKey) {
        if (valid) {
            // already signed
            return signature;
        }
        data = JSON.encode(properties);
        signature = privateKey.sign(data.getBytes(Charset.forName("UTF-8")));
        dictionary.put("data", data);
        dictionary.put("signature", Base64.encode(signature));
        valid = true;
        return signature;
    }
}
