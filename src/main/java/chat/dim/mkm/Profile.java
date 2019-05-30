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

import chat.dim.crypto.Dictionary;
import chat.dim.crypto.PublicKey;
import chat.dim.format.Base64;
import chat.dim.format.JSON;
import chat.dim.mkm.entity.ID;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class Profile extends Dictionary {

    public final ID identifier;

    protected Map<String, Object> properties;
    private String data;      // JsON.encode(properties)
    private byte[] signature; // User(identifier).sign(data)
    protected boolean valid;  // true on signature matched

    /**
     *  Public key (used for encryption, can be same with meta.key)
     *
     *      RSA
     */
    private PublicKey key;

    public Profile(Map<String, Object> dictionary) {
        super(dictionary);
        // ID
        identifier = ID.getInstance(dictionary.get("ID"));

        // properties
        properties = new HashMap<>();
        // data = JsON.encode(properties)
        data = (String) dictionary.get("data");
        // signature = User(identifier).sign(data)
        String base64 = (String) dictionary.get("signature");
        signature = (base64 == null) ? null : Base64.decode(base64);
        // verify flag
        valid = false;

        // public key
        key = null;
    }

    public Profile(ID identifier) {
        super();
        // ID
        this.identifier = identifier;
        dictionary.put("ID", identifier);

        // properties
        properties = new HashMap<>();
        // data
        data = null;
        // signature
        signature = null;
        // verify flag
        valid = false;

        // public key
        key = null;
    }

    @SuppressWarnings("unchecked")
    public static Profile getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Profile) {
            return (Profile) object;
        } else if (object instanceof Map) {
            return new Profile((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("unknown meta:" + object);
        }
    }

    /**
     *  Verify 'data' and 'signature', if OK, refresh properties from 'data'
     *
     * @param account - account with profile.identifier
     * @return true on signature matched
     */
    public boolean verify(Account account) {
        if (valid) {
            // already verified
            return true;
        }
        if (data == null || signature == null) {
            // data error
            return false;
        }
        if (!account.identifier.equals(identifier)) {
            throw new IllegalArgumentException("ID not match");
        }
        if (account.verify(data.getBytes(Charset.forName("UTF-8")), signature)) {
            valid = true;
            // refresh properties
            properties.clear();
            properties.putAll(JSON.decode(data));

            // get public key
            try {
                key = PublicKey.getInstance(properties.get("key"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            data = null;
            signature = null;
        }
        return valid;
    }

    /**
     *  Encode properties to 'data' and sign it to 'signature'
     *
     * @param user - user with profile.identifier
     * @return signature
     */
    public byte[] sign(User user) {
        if (valid) {
            // already signed
            return signature;
        }
        if (!user.identifier.equals(identifier)) {
            throw new IllegalArgumentException("ID not match");
        }
        data = JSON.encode(properties);
        signature = user.sign(data.getBytes(Charset.forName("UTF-8")));
        dictionary.put("data", data);
        dictionary.put("signature", Base64.encode(signature));
        valid = true;
        return signature;
    }

    /**
     *  Reset data signature after properties changed
     */
    protected void reset() {
        dictionary.remove("data");
        dictionary.remove("signature");
        data = null;
        signature = null;
        valid = false;
    }

    //---- properties getter/setter

    public PublicKey getKey() {
        if (!valid) {
            return null;
        }
        return key;
    }

    public void setKey(PublicKey publicKey) {
        key = publicKey;
        properties.put("key", publicKey);
        reset();
    }

    public String getName() {
        if (!valid) {
            return null;
        }
        return (String) properties.get("name");
    }

    public void setName(String name) {
        properties.put("name", name);
        reset();
    }
}
