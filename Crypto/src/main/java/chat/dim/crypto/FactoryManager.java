/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Albert Moky
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
import java.util.HashMap;
import java.util.Map;

import chat.dim.type.Wrapper;

enum FactoryManager {

    INSTANCE;

    static FactoryManager getInstance() {
        return INSTANCE;
    }

    GeneralFactory generalFactory = new GeneralFactory();

    static class GeneralFactory {

        final Map<String, SymmetricKey.Factory> symmetricKeyFactories = new HashMap<>();

        final Map<String, PublicKey.Factory> publicKeyFactories = new HashMap<>();

        final Map<String, PrivateKey.Factory> privateKeyFactories = new HashMap<>();

        // sample data for checking keys
        private static final byte[] promise = "Moky loves May Lee forever!".getBytes();

        boolean matches(SignKey sKey, VerifyKey pKey) {
            // try to verify with signature
            byte[] signature = sKey.sign(promise);
            return pKey.verify(promise, signature);
        }
        boolean matches(EncryptKey pKey, DecryptKey sKey) {
            // check by encryption
            byte[] ciphertext = pKey.encrypt(promise);
            byte[] plaintext = sKey.decrypt(ciphertext);
            return Arrays.equals(plaintext, promise);
        }

        String getAlgorithm(Map<String, Object> key) {
            return (String) key.get("algorithm");
        }

        //
        //  SymmetricKey
        //

        SymmetricKey generateSymmetricKey(String algorithm) {
            SymmetricKey.Factory factory = symmetricKeyFactories.get(algorithm);
            assert factory != null : "key algorithm not support: " + algorithm;
            return factory.generateSymmetricKey();
        }

        SymmetricKey parseSymmetricKey(Object key) {
            if (key == null) {
                return null;
            } else if (key instanceof SymmetricKey) {
                return (SymmetricKey) key;
            }
            Map<String, Object> info = Wrapper.getMap(key);
            assert info != null : "key error: " + key;
            String algorithm = getAlgorithm(info);
            assert algorithm != null : "failed to get algorithm name from key: " + key;
            SymmetricKey.Factory factory = symmetricKeyFactories.get(algorithm);
            if (factory == null) {
                factory = symmetricKeyFactories.get("*");  // unknown
                assert factory != null : "cannot parse key: " + key;
            }
            return factory.parseSymmetricKey(info);
        }

        //
        //  PrivateKey
        //

        PrivateKey generatePrivateKey(String algorithm) {
            PrivateKey.Factory factory = privateKeyFactories.get(algorithm);
            assert factory != null : "key algorithm not support: " + algorithm;
            return factory.generatePrivateKey();
        }

        PrivateKey parsePrivateKey(Object key) {
            if (key == null) {
                return null;
            } else if (key instanceof PrivateKey) {
                return (PrivateKey) key;
            }
            Map<String, Object> info = Wrapper.getMap(key);
            assert info != null : "key error: " + key;
            String algorithm = getAlgorithm(info);
            assert algorithm != null : "failed to get algorithm name from key: " + key;
            PrivateKey.Factory factory = privateKeyFactories.get(algorithm);
            if (factory == null) {
                factory = privateKeyFactories.get("*");  // unknown
                assert factory != null : "cannot parse key: " + key;
            }
            return factory.parsePrivateKey(info);
        }

        //
        //  PublicKey
        //

        PublicKey parsePublicKey(Object key) {
            if (key == null) {
                return null;
            } else if (key instanceof PublicKey) {
                return (PublicKey) key;
            }
            Map<String, Object> info = Wrapper.getMap(key);
            assert info != null : "key error: " + key;
            String algorithm = getAlgorithm(info);
            assert algorithm != null : "failed to get algorithm name from key: " + key;
            PublicKey.Factory factory = publicKeyFactories.get(algorithm);
            if (factory == null) {
                factory = publicKeyFactories.get("*");  // unknown
                assert factory != null : "cannot parse key: " + key;
            }
            return factory.parsePublicKey(info);
        }
    }
}
