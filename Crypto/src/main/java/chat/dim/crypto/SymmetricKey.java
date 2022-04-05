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

import chat.dim.type.MapWrapper;

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

    static boolean matches(EncryptKey pKey, DecryptKey sKey) {
        // check by encryption
        byte[] ciphertext = pKey.encrypt(promise);
        byte[] plaintext = sKey.decrypt(ciphertext);
        return Arrays.equals(plaintext, promise);
    }

    //
    //  Factory methods
    //
    static SymmetricKey generate(String algorithm) {
        Factory factory = getFactory(algorithm);
        if (factory == null) {
            throw new NullPointerException("key algorithm not support: " + algorithm);
        }
        return factory.generateSymmetricKey();
    }
    static SymmetricKey parse(Object key) {
        if (key == null) {
            return null;
        } else if (key instanceof SymmetricKey) {
            return (SymmetricKey) key;
        }
        Map<String, Object> info = MapWrapper.getMap(key);
        assert info != null : "key error: " + key;
        String algorithm = CryptographyKey.getAlgorithm(info);
        assert algorithm != null : "failed to get algorithm name from key: " + key;
        Factory factory = getFactory(algorithm);
        if (factory == null) {
            factory = getFactory("*");  // unknown
            assert factory != null : "cannot parse key: " + key;
        }
        return factory.parseSymmetricKey(info);
    }

    static Factory getFactory(String algorithm) {
        return Factories.symmetricKeyFactories.get(algorithm);
    }
    static void setFactory(String algorithm, Factory factory) {
        Factories.symmetricKeyFactories.put(algorithm, factory);
    }

    /**
     *  Key Factory
     *  ~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Generate key
         *
         * @return SymmetricKey
         */
        SymmetricKey generateSymmetricKey();

        /**
         *  Parse map object to key
         *
         * @param key - key info
         * @return SymmetricKey
         */
        SymmetricKey parseSymmetricKey(Map<String, Object> key);
    }
}
