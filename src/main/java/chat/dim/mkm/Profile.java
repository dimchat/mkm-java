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
import chat.dim.format.Base64;
import chat.dim.format.JSON;
import chat.dim.mkm.plugins.UserProfile;

public class Profile extends Dictionary implements TAI {

    private Object identifier = null; // ID or String

    private byte[] data = null;       // JsON.encode(properties)
    private byte[] signature = null;  // LocalUser(identifier).sign(data)

    private Map<String, Object> properties =null;
    private int status = 0;           // 1 for valid, -1 for invalid

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

    /**
     *  Create a new empty profile
     *
     * @param identifier - entity ID
     */
    public Profile(ID identifier) {
        this(identifier, null, null);
    }

    @Override
    public boolean isValid() {
        return status >= 0;
    }

    @Override
    public Object getIdentifier() {
        if (identifier == null) {
            identifier = dictionary.get("ID");
        }
        return identifier;
    }

    /**
     *  Get serialized properties
     *
     * @return JsON string
     */
    private byte[] getData() {
        if (data == null) {
            String json = (String) dictionary.get("data");
            if (json != null) {
                data = json.getBytes(Charset.forName("UTF-8"));
            }
        }
        return data;
    }

    /**
     *  Get signature for serialized properties
     *
     * @return signature data
     */
    private byte[] getSignature() {
        if (signature == null) {
            String base64 = (String) dictionary.get("signature");
            if (base64 != null) {
                signature = Base64.decode(base64);
            }
        }
        return signature;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getProperties() {
        if (status < 0) {
            // invalid
            return null;
        }
        if (properties == null) {
            String data = (String) dictionary.get("data");
            if (data == null) {
                // create new properties
                properties = new HashMap<>();
            } else {
                properties = (Map<String, Object>) JSON.decode(data);
                assert properties != null;
            }
        }
        return properties;
    }

    @Override
    public Set<String> propertyNames() {
        Map<String, Object> dict = getProperties();
        if (dict == null) {
            return null;
        }
        return dict.keySet();
    }

    @Override
    public Object getProperty(String name) {
        Map<String, Object> dict = getProperties();
        if (dict == null) {
            return null;
        }
        return dict.get(name);
    }

    @Override
    public void setProperty(String name, Object value) {
        // 1. reset status
        status = 0;
        // 2. update property value with name
        Map<String, Object> dict = getProperties();
        assert dict != null;
        if (value == null) {
            dict.remove(name);
        } else {
            dict.put(name, value);
        }
        // 3. clear data signature after properties changed
        dictionary.remove("data");
        dictionary.remove("signature");
        data = null;
        signature = null;
    }

    @Override
    public boolean verify(VerifyKey publicKey) {
        if (status == 1) {
            // already verify OK
            return true;
        }
        byte[] data = getData();
        byte[] signature = getSignature();
        if (data == null || signature == null) {
            // data error
            status = -1;
        } else if (publicKey.verify(data, signature)) {
            // signature matched
            status = 1;
        }
        // NOTICE: if status is 0, it doesn't mean the profile is invalid,
        //         try another key
        return status == 1;
    }

    @Override
    public byte[] sign(SignKey privateKey) {
        if (status == 1) {
            // already signed/verified
            assert data != null && signature != null;
            return signature;
        }
        status = 1;
        String json = JSON.encode(getProperties());
        data = json.getBytes(Charset.forName("UTF-8"));
        signature = privateKey.sign(data);
        dictionary.put("data", json);
        dictionary.put("signature", Base64.encode(signature));
        return signature;
    }

    //---- properties getter/setter

    /**
     *  Get entity name
     *
     * @return name string
     */
    public String getName() {
        return  (String) getProperty("name");
    }

    public void setName(String value) {
        setProperty("name", value);
    }

    @Override
    public EncryptKey getKey() {
        // TODO: get public key if this is user profile
        return null;
    }

    public void setKey(EncryptKey publicKey) {
        // TODO: set public key if this is user profile
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

    static {
        // user profile
        register(UserProfile.class);
    }
}

