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

import java.util.List;

import chat.dim.crypto.DecryptKey;
import chat.dim.crypto.EncryptKey;
import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;

/**
 *  User Data Source
 *  ~~~~~~~~~~~~~~~~
 *
 *  (Encryption/decryption)
 *  1. public key for encryption
 *     if profile.key not exists, means it is the same key with meta.key
 *  2. private keys for decryption
 *     the private keys paired with [profile.key, meta.key]
 *
 *  (Signature/Verification)
 *  3. private key for signature
 *     the private key paired with meta.key
 *  4. public keys for verification
 *     [meta.key]
 */
public interface UserDataSource extends EntityDataSource {

    /**
     *  Get contacts list
     *
     * @param user - user ID
     * @return contacts list (ID)
     */
    List<ID> getContacts(ID user);

    /**
     *  Get user's public key for encryption
     *  (profile.key or meta.key)
     *
     * @param user - user ID
     * @return public key
     */
    EncryptKey getPublicKeyForEncryption(ID user);

    /**
     *  Get user's private keys for decryption
     *  (which paired with [profile.key, meta.key])
     *
     * @param user - user ID
     * @return private keys
     */
    List<DecryptKey> getPrivateKeysForDecryption(ID user);

    /**
     *  Get user's private key for signature
     *  (which paired with profile.key or meta.key)
     *
     * @param user - user ID
     * @return private key
     */
    SignKey getPrivateKeyForSignature(ID user);

    /**
     *  Get user's public keys for verification
     *  [profile.key, meta.key]
     *
     * @param user - user ID
     * @return public keys
     */
    List<VerifyKey> getPublicKeysForVerification(ID user);
}