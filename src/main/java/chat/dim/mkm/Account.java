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
import chat.dim.mkm.entity.Entity;
import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.Meta;

public class Account extends Entity {

    public Account(ID identifier) {
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
        // get key for signature from meta
        Meta meta = getMeta();
        if (meta == null) {
            throw new NullPointerException("failed to get meta.key for:" + identifier);
        } else {
            key = meta.key;
        }
        return key.verify(data, signature);
    }

    /**
     *  Encrypt data, try profile.key first, if not found, use meta.key
     *
     * @param plaintext - message data
     * @return encrypted data
     */
    public byte[] encrypt(byte[] plaintext) {
        PublicKey key;
        // get key for encryption from profile
        Profile profile = getProfile();
        if (profile == null) {
            // profile not updated?
            key = null;
        } else {
            key = profile.getKey();
        }
        if (key == null) {
            // get key for encryption from meta
            Meta meta = getMeta();
            if (meta == null) {
                throw new NullPointerException("failed to get meta.key for:" + identifier);
            } else {
                key = meta.key;
            }
        }
        return key.encrypt(plaintext);
    }
}
