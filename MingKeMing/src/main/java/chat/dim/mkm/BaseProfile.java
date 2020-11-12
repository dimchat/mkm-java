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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import chat.dim.Entity;
import chat.dim.crypto.EncryptKey;
import chat.dim.crypto.PublicKey;
import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;
import chat.dim.format.Base64;
import chat.dim.format.JSONMap;
import chat.dim.format.UTF8;
import chat.dim.protocol.ID;
import chat.dim.protocol.Profile;
import chat.dim.type.Dictionary;

public class BaseProfile extends Dictionary<String, Object> implements Profile {

    private byte[] data = null;       // JsON.encode(properties)
    private byte[] signature = null;  // LocalUser(identifier).sign(data)

    private Map<String, Object> properties = null;
    private int status = 0;           // 1 for valid, -1 for invalid

    private ID identifier = null;
    private EncryptKey key = null;

    /**
     *  Create Profile
     *
     *  @param dictionary - profile info
     */
    public BaseProfile(Map<String, Object> dictionary) {
        super(dictionary);
    }

    /**
     *  Create profile with data and signature
     *
     * @param identifier - entity ID
     * @param data - profile data in JsON format
     * @param signature - signature of profile data
     */
    public BaseProfile(ID identifier, String data, byte[] signature) {
        super();
        // ID
        put("ID", identifier);

        // json data
        if (data != null) {
            put("data", data);
        }
        // signature
        if (signature != null) {
            put("signature", Base64.encode(signature));
        }
    }

    /**
     *  Create a new empty profile
     *
     * @param identifier - entity ID
     */
    public BaseProfile(ID identifier) {
        this(identifier, null, null);
    }

    @Override
    public boolean isValid() {
        return status >= 0;
    }

    @Override
    public ID getIdentifier() {
        if (identifier == null) {
            identifier = Entity.parseID(get("ID"));
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
            String json = (String) get("data");
            if (json != null) {
                data = UTF8.encode(json);
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
            String base64 = (String) get("signature");
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
            String data = (String) get("data");
            if (data == null) {
                // create new properties
                properties = new HashMap<>();
            } else {
                Map info = JSONMap.decode(UTF8.encode(data));
                assert info != null : "profile data error: " + data;
                properties = (Map<String, Object>) info;
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
        assert status >= 0 : "status error: " + this;
        status = 0;
        // 2. update property value with name
        Map<String, Object> dict = getProperties();
        assert dict != null : "failed to get properties: " + this;
        if (value == null) {
            dict.remove(name);
        } else {
            dict.put(name, value);
        }
        // 3. clear data signature after properties changed
        remove("data");
        remove("signature");
        data = null;
        signature = null;
    }

    @Override
    public boolean verify(VerifyKey publicKey) {
        if (status > 0) {
            // already verify OK
            return true;
        }
        byte[] data = getData();
        byte[] signature = getSignature();
        if (data == null) {
            // NOTICE: if data is empty, signature should be empty at the same time
            //         this happen while profile not found
            if (signature == null) {
                status = 0;
            } else {
                // data signature error
                status = -1;
            }
        } else if (signature == null) {
            // signature error
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
        if (status > 0) {
            // already signed/verified
            assert data != null && signature != null : "profile data/signature error";
            return signature;
        }
        status = 1;
        data = JSONMap.encode(getProperties());
        signature = privateKey.sign(data);
        put("data", UTF8.decode(data));
        put("signature", Base64.encode(signature));
        return signature;
    }

    //---- properties getter/setter

    /**
     *  Get entity name
     *
     * @return name string
     */
    @Override
    public String getName() {
        return (String) getProperty("name");
    }

    @Override
    public void setName(String value) {
        setProperty("name", value);
    }

    /**
     *  Public key (used for encryption, can be same with meta.key)
     *
     *      RSA
     */
    @Override
    public EncryptKey getKey() {
        if (key == null) {
            Object info = getProperty("key");
            if (info instanceof Map) {
                try {
                    //noinspection unchecked
                    PublicKey pKey = PublicKey.getInstance((Map<String, Object>) info);
                    if (pKey instanceof EncryptKey) {
                        key = (EncryptKey) pKey;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return key;
    }

    @Override
    public void setKey(EncryptKey publicKey) {
        setProperty("key", publicKey);
        key = publicKey;
    }
}

