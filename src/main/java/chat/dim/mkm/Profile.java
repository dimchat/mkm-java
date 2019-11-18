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

import chat.dim.crypto.EncryptKey;
import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;
import chat.dim.crypto.impl.PublicKeyImpl;
import chat.dim.format.Base64;
import chat.dim.format.JSON;

public class Profile extends TAI {

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

    //---- properties getter/setter

    /**
     *  Get entity name
     *
     * @return name string
     */
    @SuppressWarnings("unchecked")
    public String getName() {
        String name = (String) getProperty("name");
        if (name != null) {
            return name;
        }
        // get from 'names'
        Object array = getProperty("names");
        if (array != null) {
            List<String> names = (List<String>) array;
            if (names.size() > 0) {
                return names.get(0);
            }
        }
        return null;
    }

    public void setName(String value) {
        setProperty("name", value);
    }


    /**
     *  Public key (used for encryption, can be same with meta.key)
     *
     *      RSA
     */
    private EncryptKey key = null;
    
    public EncryptKey getKey() {
        if (key == null) {
            // get public key
            try {
                key = (EncryptKey) PublicKeyImpl.getInstance(getProperty("key"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return key;
    }

    public void setKey(EncryptKey publicKey) {
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
        return new Profile(dictionary);
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

    private String data = null;       // JsON.encode(properties)
    private byte[] signature = null;  // LocalUser(identifier).sign(data)

    private Map<String, Object> properties =null;
    private int status = 0;           // 1 for valid, -1 for invalid

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
        return status >= 0;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getProperties() {
        if (status == -1) {
            // invalid
            return null;
        }
        if (properties == null) {
            String data = getData();
            if (data == null) {
                // create new properties
                properties = new HashMap<>();
            } else {
                Object dict = JSON.decode(data);
                assert dict instanceof Map;
                properties = (Map<String, Object>) dict;
            }
        }
        return properties;
    }

    /**
     *  Get all keys for properties
     *
     * @return profile properties key set
     */
    public Set<String> propertyKeys() {
        Map<String, Object> dict = getProperties();
        if (dict == null) {
            return null;
        }
        return dict.keySet();
    }

    /**
     *  Get profile property data with key
     *
     * @param key - property key
     * @return property data
     */
    public Object getProperty(String key) {
        Map<String, Object> dict = getProperties();
        if (dict == null) {
            return null;
        }
        return dict.get(key);
    }

    /**
     *  Update profile property with key and data
     *  (this will reset 'data' and 'signature')
     *
     * @param key - property key
     * @param value - property data
     */
    public void setProperty(String key, Object value) {
        status = 0;
        Map<String, Object> dict = getProperties();
        assert dict != null;
        // 1. update value with key in properties
        if (value == null) {
            dict.remove(key);
        } else {
            dict.put(key, value);
        }
        // 2. reset data signature after properties changed
        dictionary.remove("data");
        dictionary.remove("signature");
        data = null;
        signature = null;
    }

    /**
     *  Verify 'data' and 'signature' with public key
     *
     * @param publicKey - public key in meta.key
     * @return true on signature matched
     */
    public boolean verify(VerifyKey publicKey) {
        if (status == 1) {
            // already verify OK
            return true;
        }
        String data = getData();
        byte[] signature = getSignature();
        if (data == null || signature == null) {
            // data error
            status = -1;
        } else if (publicKey.verify(data.getBytes(Charset.forName("UTF-8")), signature)) {
            // signature matched
            status = 1;
        }
        return status == 1;
    }

    /**
     *  Encode properties to 'data' and sign it to 'signature'
     *
     * @param privateKey - private key match meta.key
     * @return signature
     */
    public byte[] sign(SignKey privateKey) {
        if (status == 1) {
            // already signed/verified
            return signature;
        }
        status = 1;
        data = JSON.encode(getProperties());
        signature = privateKey.sign(data.getBytes(Charset.forName("UTF-8")));
        dictionary.put("data", data);
        dictionary.put("signature", Base64.encode(signature));
        return signature;
    }
}
