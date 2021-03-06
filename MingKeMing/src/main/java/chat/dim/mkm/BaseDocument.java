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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;
import chat.dim.format.Base64;
import chat.dim.format.JSONMap;
import chat.dim.format.UTF8;
import chat.dim.protocol.Document;
import chat.dim.protocol.ID;
import chat.dim.type.Dictionary;

public class BaseDocument extends Dictionary implements Document {

    private ID identifier;

    private byte[] data;       // JsON.encode(properties)
    private byte[] signature;  // LocalUser(identifier).sign(data)

    private Map<String, Object> properties;
    private int status;        // 1 for valid, -1 for invalid

    /**
     *  Create Entity Document
     *
     *  @param dictionary - info
     */
    public BaseDocument(Map<String, Object> dictionary) {
        super(dictionary);
        // lazy
        identifier = null;

        data = null;
        signature = null;

        properties = null;

        status = 0;
    }

    /**
     *  Create entity document with data and signature loaded from local storage
     *
     * @param identifier - entity ID
     * @param data - document data in JsON format
     * @param signature - signature of document data
     */
    public BaseDocument(ID identifier, byte[] data, byte[] signature) {
        super();

        // ID
        put("ID", identifier.toString());
        this.identifier = identifier;

        // json data
        put("data", UTF8.decode(data));
        this.data = data;

        // signature
        put("signature", Base64.encode(signature));
        this.signature = signature;

        properties = null;

        // all documents must be verified before saving into local storage
        status = 1;
    }

    /**
     *  Create a new empty document
     *
     * @param identifier - entity ID
     * @param type       - document type
     */
    public BaseDocument(ID identifier, String type) {
        super();

        // ID
        put("ID", identifier.toString());
        this.identifier = identifier;

        data = null;
        signature = null;

        if (type != null && type.length() > 0) {
            properties = new HashMap<>();
            properties.put("type", type);
        } else {
            properties = null;
        }

        status = 0;
    }

    @Override
    public boolean isValid() {
        return status > 0;
    }

    @Override
    public String getType() {
        String type = (String) getProperty("type");
        if (type == null) {
            type = Document.getType(getMap());
        }
        return type;
    }

    @Override
    public ID getIdentifier() {
        if (identifier == null) {
            identifier = Document.getIdentifier(getMap());
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

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getProperties() {
        if (status < 0) {
            // invalid
            return null;
        }
        if (properties == null) {
            byte[] data = getData();
            if (data == null) {
                // create new properties
                properties = new HashMap<>();
            } else {
                Map info = JSONMap.decode(data);
                assert info != null : "document data error: " + getMap();
                properties = (Map<String, Object>) info;
            }
        }
        return properties;
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
            //         this happen while entity document not found
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
        // NOTICE: if status is 0, it doesn't mean the entity document is invalid,
        //         try another key
        return status == 1;
    }

    @Override
    public byte[] sign(SignKey privateKey) {
        if (status > 0) {
            // already signed/verified
            assert data != null && signature != null : "document data/signature error";
            return signature;
        }
        // update sign time
        Date now = new Date();
        setProperty("time", now.getTime() / 1000);
        // update status
        status = 1;
        // sign
        data = JSONMap.encode(getProperties());
        signature = privateKey.sign(data);
        put("data", UTF8.decode(data));
        put("signature", Base64.encode(signature));
        return signature;
    }

    //---- properties getter/setter

    @Override
    public Date getTime() {
        Object timestamp = getProperty("time");
        if (timestamp == null) {
            return null;
        }
        return new Date(((Number) timestamp).longValue() * 1000);
    }

    @Override
    public String getName() {
        return (String) getProperty("name");
    }

    @Override
    public void setName(String value) {
        setProperty("name", value);
    }
}

