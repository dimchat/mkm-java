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

import java.security.InvalidParameterException;
import java.util.List;

import chat.dim.crypto.PrivateKey;

/**
 *  User for communication
 *  ~~~~~~~~~~~~~~~~~~~~~~
 *  This class is for creating user
 *
 *      functions:
 *          sign(data)    - calculate signature of (encrypted content) data
 *          decrypt(data) - decrypt (symmetric key) data
 */
public class LocalUser extends User {

    public LocalUser(ID identifier) {
        super(identifier);
    }

    @Override
    public UserDataSource getDataSource() {
        return (UserDataSource) super.getDataSource();
    }

    private PrivateKey getSignKey() {
        return getDataSource().getPrivateKeyForSignature(identifier);
    }

    private List<PrivateKey> getDecryptKeys() {
        return getDataSource().getPrivateKeysForDecryption(identifier);
    }

    /**
     *  Get all contacts of the user
     *
     * @return contact list
     */
    public List<ID> getContacts() {
        return getDataSource().getContacts(identifier);
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
