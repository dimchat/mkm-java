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
package chat.dim;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import chat.dim.crypto.DecryptKey;
import chat.dim.crypto.EncryptKey;
import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;
import chat.dim.protocol.Document;
import chat.dim.protocol.ID;
import chat.dim.protocol.Visa;

/**
 *  User account for communication
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *  This class is for creating user account
 *
 *  functions:
 *      (User)
 *      1. verify(data, signature) - verify (encrypted content) data and signature
 *      2. encrypt(data)           - encrypt (symmetric key) data
 *      (LocalUser)
 *      3. sign(data)    - calculate signature of (encrypted content) data
 *      4. decrypt(data) - decrypt (symmetric key) data
 */
public class User extends Entity {

    public User(ID identifier) {
        super(identifier);
    }

    @Override
    public UserDataSource getDataSource() {
        return (UserDataSource) super.getDataSource();
    }

    /**
     *  Get all contacts of the user
     *
     * @return contact list
     */
    public List<ID> getContacts() {
        return getDataSource().getContacts(identifier);
    }

    private EncryptKey getVisaKey() {
        Document doc = getDocument(Document.VISA);
        if (doc == null || !doc.isValid()) {
            // document not found or not valid
            return null;
        }
        if (doc instanceof Visa) {
            return ((Visa) doc).getKey();
        }
        return null;
    }

    // NOTICE: meta.key will never changed, so use visa.key to encrypt
    //         is a better way
    private EncryptKey getEncryptKey() {
        // 1. get key from visa
        EncryptKey key = getVisaKey();
        if (key != null) {
            return key;
        }
        // 2. get key from meta
        Object mKey = getMeta().getKey();
        if (mKey instanceof EncryptKey) {
            return (EncryptKey) mKey;
        }
        throw new NullPointerException("failed to get encrypt key for user: " + identifier);
    }

    // NOTICE: I suggest using the private key paired with meta.key to sign message
    //         so here should return the meta.key
    private List<VerifyKey> getVerifyKeys() {
        // 0. get keys from data source
        List<VerifyKey> keys = getDataSource().getPublicKeysForVerification(identifier);
        if (keys != null && keys.size() > 0) {
            return keys;
        }
        keys = new ArrayList<>();
        // 1. get key from visa
        Object pKey = getVisaKey();
        if (pKey instanceof VerifyKey) {
            // the sender may use communication key to sign message.data,
            // so try to verify it with visa.key here
            keys.add((VerifyKey) pKey);
        }
        // 2. get key from meta
        VerifyKey mKey = getMeta().getKey();
        // the sender may use identity key to sign message.data,
        // try to verify it with meta.key
        keys.add(mKey);
        return keys;
    }

    /**
     *  Verify data and signature with user's public keys
     *
     * @param data - message data
     * @param signature - message signature
     * @return true on correct
     */
    public boolean verify(byte[] data, byte[] signature) {
        List<VerifyKey> keys = getVerifyKeys();
        for (VerifyKey key : keys) {
            if (key.verify(data, signature)) {
                // matched!
                return true;
            }
        }
        return false;
    }

    /**
     *  Encrypt data, try visa.key first, if not found, use meta.key
     *
     * @param plaintext - message data
     * @return encrypted data
     */
    public byte[] encrypt(byte[] plaintext) {
        EncryptKey key = getEncryptKey();
        assert key != null : "failed to get encrypt key for user: " + identifier;
        return key.encrypt(plaintext);
    }

    //
    //  Interfaces for Local User
    //

    /**
     *  Sign data with user's private key
     *
     * @param data - message data
     * @return signature
     */
    public byte[] sign(byte[] data) {
        // NOTICE: I suggest use the private key which paired to visa.key
        //         to sign message
        SignKey key = getDataSource().getPrivateKeyForSignature(identifier);
        assert key != null : "failed to get sign key for user: " + identifier;
        return key.sign(data);
    }

    /**
     *  Decrypt data with user's private key(s)
     *
     * @param ciphertext - encrypted data
     * @return plain text
     */
    public byte[] decrypt(byte[] ciphertext) {
        // NOTICE: if you provide a public key in visa for encryption
        //         here you should return the private key paired with visa.key
        List<DecryptKey> keys = getDataSource().getPrivateKeysForDecryption(identifier);
        assert keys != null && keys.size() > 0 : "failed to get decrypt keys for user: " + identifier;
        byte[] plaintext;
        for (DecryptKey key : keys) {
            // try decrypting it with each private key
            try {
                plaintext = key.decrypt(ciphertext);
                if (plaintext != null) {
                    // OK!
                    return plaintext;
                }
            } catch (InvalidParameterException e) {
                // this key not match, try next one
                //e.printStackTrace();
            }
        }
        // decryption failed
        return null;
    }

    //
    //  Interfaces for Visa
    //
    public Visa sign(Visa doc) {
        if (!identifier.equals(doc.getIdentifier())) {
            // visa ID not match
            return null;
        }
        SignKey key = getDataSource().getPrivateKeyForVisaSignature(identifier);
        assert key != null : "failed to get sign key for user: " + identifier;
        doc.sign(key);
        return doc;
    }

    public boolean verify(Visa doc) {
        if (!identifier.equals(doc.getIdentifier())) {
            return false;
        }
        // if meta not exists, user won't be created
        VerifyKey key = getMeta().getKey();
        return doc.verify(key);
    }
}
