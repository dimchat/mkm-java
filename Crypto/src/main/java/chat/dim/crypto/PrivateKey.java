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
package chat.dim.crypto;

import java.util.Map;

/**
 *  Asymmetric Cryptography Private Key
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *  This class is used to decrypt symmetric key or sign message data
 *
 *  key data format: {
 *      algorithm : "RSA", // "ECC", ...
 *      data      : "{BASE64_ENCODE}",
 *      ...
 *  }
 */
public interface PrivateKey extends SignKey {

    /**
     *  Get public key from private key
     *
     * @return public key paired to this private key
     */
    PublicKey getPublicKey();

    static boolean isEqual(PrivateKey key1, PrivateKey key2) {
        if (key1 == key2) {
            // same object
            return true;
        }
        // check by public key
        PublicKey publicKey = key1.getPublicKey();
        if (publicKey == null) {
            throw new NullPointerException("failed to get public key: " + key1);
        }
        return isMatch(publicKey, key2);
    }

    static boolean isMatch(PublicKey pKey, PrivateKey sKey) {
        // 1. if the SK has the same public key, return true
        PublicKey publicKey = sKey.getPublicKey();
        if (publicKey == pKey) {
            return true;
        }
        // 2. try to verify the SK's signature
        byte[] signature = sKey.sign(promise);
        return pKey.verify(promise, signature);
    }

    //
    //  Factory methods
    //
    static PrivateKey generate(String algorithm) {
        return Factories.privateKeyFactory.generatePrivateKey(algorithm);
    }
    static PrivateKey parse(Map<String, Object> key) {
        if (key == null) {
            return null;
        } else if (key instanceof PrivateKey) {
            return (PrivateKey) key;
        }
        return Factories.privateKeyFactory.parsePrivateKey(key);
    }

    /**
     *  Key Factory
     *  ~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Generate key with algorithm
         *
         * @param algorithm - key algorithm
         * @return PrivateKey
         */
        PrivateKey generatePrivateKey(String algorithm);

        /**
         *  Parse map object to key
         *
         * @param key - key info
         * @return PrivateKey
         */
        PrivateKey parsePrivateKey(Map<String, Object> key);
    }
}
