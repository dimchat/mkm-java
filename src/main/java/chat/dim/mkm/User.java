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

import chat.dim.crypto.PublicKey;

/**
 *  User account for communication
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *  This class is for creating account
 *
 *      functions:
 *          verify(data, signature) - verify (encrypted content) data and signature
 *          encrypt(data)           - encrypt (symmetric key) data
 */
public class User extends Entity {

    public User(ID identifier) {
        super(identifier);
    }

    /**
     *  Verify data with signature, use meta.key
     *
     * @param data - message data
     * @param signature - message signature
     * @return true on correct
     */
    public boolean verify(byte[] data, byte[] signature) {
        PublicKey key;
        /*
        // 1. get key for signature from profile
        key = getProfileKey();
        if (key != null && key.verify(data, signature)) {
            return true;
        }
        */
        // 2. get key for signature from meta
        key = getMetaKey();
        if (key == null) {
            throw new NullPointerException("failed to get verify key for: " + identifier);
        }
        // 3. verify with meta.key
        return key.verify(data, signature);
    }

    /**
     *  Encrypt data, try profile.key first, if not found, use meta.key
     *
     * @param plaintext - message data
     * @return encrypted data
     */
    public byte[] encrypt(byte[] plaintext) {
        // 1. get key for encryption from profile
        PublicKey key = getProfileKey();
        if (key == null) {
            // 2. get key for encryption from meta instead
            //    NOTICE: meta.key will never changed, so use profile.key to encrypt is the better way
            key = getMetaKey();
        }
        if (key == null) {
            throw new NullPointerException("failed to get encrypt key for: " + identifier);
        }
        // 3. encrypt with profile.key
        return key.encrypt(plaintext);
    }

    private PublicKey getMetaKey() {
        Meta meta = getMeta();
        return meta == null ? null : meta.key;
    }

    private PublicKey getProfileKey() {
        Profile profile = getProfile();
        return profile == null ? null : profile.getKey();
    }

    @Override
    public Profile getProfile() {
        Profile profile = super.getProfile();
        if (profile == null || profile.isValid()) {
            return profile;
        }
        // try to verify with meta.key
        PublicKey key = getMetaKey();
        if (key != null && profile.verify(key)) {
            // signature correct
            return profile;
        }
        // profile error?
        return profile;
    }
}
