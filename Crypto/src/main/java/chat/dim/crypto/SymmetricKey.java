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

import java.util.Arrays;
import java.util.Map;

/**
 *  Symmetric Cryptography Key
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~
 *  This class is used to encrypt or decrypt message data
 *
 *  key data format: {
 *      algorithm : "AES", // "DES", ...
 *      data      : "{BASE64_ENCODE}",
 *      ...
 *  }
 */
public interface SymmetricKey extends EncryptKey, DecryptKey {

    String AES = "AES"; //-- "AES/CBC/PKCS7Padding"
    String DES = "DES";

    static boolean isEqual(SymmetricKey key1, SymmetricKey key2) {
        if (key1 == key2) {
            // same object
            return true;
        }
        // check by encryption
        byte[] ciphertext = key1.encrypt(promise);
        byte[] plaintext = key2.decrypt(ciphertext);
        return Arrays.equals(plaintext, promise);
    }

    //
    //  Factory methods
    //
    static SymmetricKey generate(String algorithm) {
        return Factories.symmetricKeyFactory.generateSymmetricKey(algorithm);
    }
    static SymmetricKey parse(Map<String, Object> key) {
        if (key == null) {
            return null;
        } else if (key instanceof SymmetricKey) {
            return (SymmetricKey) key;
        }
        return Factories.symmetricKeyFactory.parseSymmetricKey(key);
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
         * @return SymmetricKey
         */
        SymmetricKey generateSymmetricKey(String algorithm);

        /**
         *  Parse map object to key
         *
         * @param key - key info
         * @return SymmetricKey
         */
        SymmetricKey parseSymmetricKey(Map<String, Object> key);
    }
}
