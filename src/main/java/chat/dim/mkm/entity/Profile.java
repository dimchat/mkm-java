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
package chat.dim.mkm.entity;

import chat.dim.crypto.Dictionary;
import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
import chat.dim.format.Base64;
import chat.dim.format.JSON;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class Profile extends TAO {

    private String name;

    public Profile(Map<String, Object> dictionary) {
        super(dictionary);
    }

    public Profile(ID identifier, String data, byte[] signature) {
        super(identifier, data, signature);
    }

    public Profile(ID identifier) {
        this(identifier, null, null);
    }

    //---- properties getter/setter

    public String getName() {
        if (!valid) {
            return null;
        }
        if (name == null) {
            name = (String) properties.get("name");
        }
        return name;
    }

    public void setName(String value) {
        name = value;
        properties.put("name", value);
        reset();
    }
}

/**
 *  The Additional Object
 *
 *      'Meta' is the information for entity which never changed, which contains the key for verify signature;
 *      'TAO' is the variable part, which contains the key for asymmetric encryption.
 */
class TAO extends Dictionary {

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

    public TAO(Map<String, Object> dictionary) {
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

    public TAO(ID identifier, String data, byte[] signature) {
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

        // public key
        this.key = null;
    }

    public PublicKey getKey() {
        return valid ? key : null;
    }

    public void setKey(PublicKey publicKey) {
        key = publicKey;
        properties.put("key", publicKey);
        reset();
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

    /**
     *  Verify 'data' and 'signature', if OK, refresh properties from 'data'
     *
     * @param publicKey - public key in meta.key
     * @return true on signature matched
     */
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
