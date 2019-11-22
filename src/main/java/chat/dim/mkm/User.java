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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import chat.dim.crypto.*;

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

    private VerifyKey getMetaKey() {
        Meta meta = getMeta();
        assert meta != null; // if meta not exists, user won't be created
        return meta.getKey();
    }

    private EncryptKey getProfileKey() {
        Profile profile = getProfile();
        if (profile == null || !profile.isValid()) {
            // profile not found or not valid
            return null;
        }
        return profile.getKey();
    }

    // NOTICE: meta.key will never changed, so use profile.key to encrypt
    //         is the better way
    private EncryptKey getEncryptKey() {
        // 0. get key from data source
        EncryptKey key = getDataSource().getPublicKeyForEncryption(identifier);
        if (key != null) {
            return key;
        }
        // 1. get key from profile
        key = getProfileKey();
        if (key != null) {
            return key;
        }
        // 2. get key from meta
        Object mKey = getMetaKey();
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
        /*
        // 1. get key from profile
        Object pKey = getProfileKey();
        if (pKey instanceof VerifyKey) {
            keys.add((VerifyKey) pKey);
        }
        */
        // 2. get key from meta
        VerifyKey mKey = getMetaKey();
        assert mKey != null;
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
     *  Encrypt data, try profile.key first, if not found, use meta.key
     *
     * @param plaintext - message data
     * @return encrypted data
     */
    public byte[] encrypt(byte[] plaintext) {
        EncryptKey key = getEncryptKey();
        assert key != null;
        return key.encrypt(plaintext);
    }

    //
    //  Interfaces for Local User
    //

    // NOTICE: I suggest use the private key which paired to meta.key
    //         to sign message
    private SignKey getSignKey() {
        return getDataSource().getPrivateKeyForSignature(identifier);
    }

    // NOTICE: if you provide a public key in profile for encryption
    //         here you should return the private key paired with profile.key
    private List<DecryptKey> getDecryptKeys() {
        return getDataSource().getPrivateKeysForDecryption(identifier);
    }

    /**
     *  Sign data with user's private key
     *
     * @param data - message data
     * @return signature
     */
    public byte[] sign(byte[] data) {
        SignKey key = getSignKey();
        assert key != null;
        return key.sign(data);
    }

    /**
     *  Decrypt data with user's private key(s)
     *
     * @param ciphertext - encrypted data
     * @return plain text
     */
    public byte[] decrypt(byte[] ciphertext) {
        byte[] plaintext;
        List<DecryptKey> keys = getDecryptKeys();
        assert keys != null && keys.size() > 0;
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
}
