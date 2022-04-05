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

import chat.dim.type.MapWrapper;

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

    //
    //  Factory methods
    //
    static PrivateKey generate(String algorithm) {
        Factory factory = getFactory(algorithm);
        if (factory == null) {
            throw new NullPointerException("key algorithm not support: " + algorithm);
        }
        return factory.generatePrivateKey();
    }
    static PrivateKey parse(Object key) {
        if (key == null) {
            return null;
        } else if (key instanceof PrivateKey) {
            return (PrivateKey) key;
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
        return factory.parsePrivateKey(info);
    }

    static Factory getFactory(String algorithm) {
        return Factories.privateKeyFactories.get(algorithm);
    }
    static void setFactory(String algorithm, Factory factory) {
        Factories.privateKeyFactories.put(algorithm, factory);
    }

    /**
     *  Key Factory
     *  ~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Generate key
         *
         * @return PrivateKey
         */
        PrivateKey generatePrivateKey();

        /**
         *  Parse map object to key
         *
         * @param key - key info
         * @return PrivateKey
         */
        PrivateKey parsePrivateKey(Map<String, Object> key);
    }
}
