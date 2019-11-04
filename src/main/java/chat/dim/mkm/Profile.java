/* license: https://mit-license.org
 *
 *  Ming-Ke-Ming : Decentralized User Identity Authentication
 *
 *                                Written in 2019 by Moky <albert.moky@gmail.com>
 *
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
    private String name = null;

    /**
     *  Public key (used for encryption, can be same with meta.key)
     *
     *      RSA
     */
    private PublicKey key = null;

    /**
     *  Called by 'getInstance()' to create Profile
     *
     *  @param dictionary - profile info
     */
    public Profile(Map<String, Object> dictionary) {
        super(dictionary);
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
        name = (String) getProperty("name");

        // get public key
        try {
            key = PublicKeyImpl.getInstance(getProperty("key"));
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
        setProperty("name", name);
    }

    public PublicKey getKey() {
        return key;
    }

    public void setKey(PublicKey publicKey) {
        key = publicKey;
        setProperty("key", key);
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

    private Object identifier = null; // ID or String

    private Map<String, Object> properties = new HashMap<>();
    private String data = null;       // JsON.encode(properties)
    private byte[] signature = null;  // LocalUser(identifier).sign(data)

    private boolean valid = false;    // true on signature matched

    TAI(Map<String, Object> map) {
        super(map);
    }

    TAI(ID identifier, String data, byte[] signature) {
        super();
        // ID
        dictionary.put("ID", identifier);

        // json data
        if (data != null) {
            dictionary.put("data", data);
        }
        // signature
        if (signature != null) {
            dictionary.put("signature", Base64.encode(signature));
        }
    }

    public Object getIdentifier() {
        if (identifier == null) {
            identifier = dictionary.get("ID");
        }
        return identifier;
    }

    protected String getData() {
        if (data == null) {
            data = (String) dictionary.get("data");
        }
        return data;
    }

    protected byte[] getSignature() {
        if (signature == null) {
            String base64 = (String) dictionary.get("signature");
            if (base64 != null) {
                signature = Base64.decode(base64);
            }
        }
        return signature;
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
    public void setProperty(String key, Object value) {
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
    public Object getProperty(String key) {
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
    public Set<String> propertyKeys() {
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
        String data = getData();
        byte[] signature = getSignature();
        if (data == null || signature == null) {
            // data error
            return false;
        }
        if (publicKey.verify(data.getBytes(Charset.forName("UTF-8")), signature)) {
            // refresh properties
            properties.clear();
            Map<String, Object> dictionary = (Map<String, Object>) JSON.decode(data);
            properties.putAll(dictionary);
            // set valid
            valid = true;
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
            // already signed/verified
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
