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

import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;

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

    @Override
    public Profile getProfile() {
        Profile tai = super.getProfile();
        if (tai == null || tai.isValid()) {
            // no need to verify
            return tai;
        }
        // try to verify with meta.key
        PublicKey key = getMetaKey();
        if (tai.verify(key)) {
            // signature correct
            return tai;
        }
        // profile error? continue to process by subclass
        return tai;
    }

    private PublicKey getMetaKey() {
        Meta meta = getMeta();
        assert meta != null;
        return meta.key;
    }

    private PublicKey getProfileKey() {
        Profile profile = getProfile();
        if (profile == null) {
            return null;
        }
        return profile.getKey();
    }

    // NOTICE: meta.key will never changed, so use profile.key to encrypt
    //         is the better way
    private PublicKey getEncryptKey() {
        // 0. get key from data source
        PublicKey key = getDataSource().getPublicKeyForEncryption(identifier);
        if (key != null) {
            return key;
        }
        // 1. get key from profile
        key = getProfileKey();
        if (key != null) {
            return key;
        }
        // 2. get key for encryption from meta instead
        return getMetaKey();
    }

    private List<PublicKey> getVerifyKeys() {
        // 0. get keys from data source
        List<PublicKey> keys = getDataSource().getPublicKeysForVerification(identifier);
        if (keys != null && keys.size() > 0) {
            return keys;
        }
        keys = new ArrayList<>();
        PublicKey key;
        // 1. get key from profile
        key = getProfileKey();
        if (key != null) {
            keys.add(key);
        }
        // 2. get public key from meta
        key = getMetaKey();
        keys.add(key);
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
        List<PublicKey> keys = getVerifyKeys();
        for (PublicKey key : keys) {
            if (key.verify(data, signature)) {
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
        PublicKey key = getEncryptKey();
        assert key != null;
        return key.encrypt(plaintext);
    }

    //-------- interfaces for local user

    private PrivateKey getSignKey() {
        return getDataSource().getPrivateKeyForSignature(identifier);
    }

    private List<PrivateKey> getDecryptKeys() {
        return getDataSource().getPrivateKeysForDecryption(identifier);
    }

    /**
     *  Sign data with user's private key
     *
     * @param data - message data
     * @return signature
     */
    public byte[] sign(byte[] data) {
        PrivateKey key = getSignKey();
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
        List<PrivateKey> keys = getDecryptKeys();
        for (PrivateKey key : keys) {
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
