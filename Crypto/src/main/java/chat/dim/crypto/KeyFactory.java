/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Albert Moky
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

public class KeyFactory {

    private static final byte[] promise = "Moky loves May Lee forever!".getBytes();

    public static boolean isEqual(SymmetricKey key1, SymmetricKey key2) {
        if (key1 == key2) {
            // same object
            return true;
        }
        // check by encryption
        byte[] ciphertext = key1.encrypt(promise);
        byte[] plaintext = key2.decrypt(ciphertext);
        return Arrays.equals(plaintext, promise);
    }

    public static boolean isEqual(PrivateKey key1, PrivateKey key2) {
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

    public static boolean isMatch(PublicKey pKey, PrivateKey sKey) {
        // 1. if the SK has the same public key, return true
        PublicKey publicKey = sKey.getPublicKey();
        if (publicKey == pKey) {
            return true;
        }
        // 2. try to verify the SK's signature
        byte[] signature = sKey.sign(promise);
        return pKey.verify(promise, signature);
    }

    public static SymmetricKey getSymmetricKey(String algorithm) {
        return parser.generateSymmetricKey(algorithm);
    }

    public static PrivateKey getPrivateKey(String algorithm) {
        return parser.generatePrivateKey(algorithm);
    }

    public static PrivateKey getPrivateKey(Map<String, Object> key) {
        if (key == null) {
            return null;
        } else if (key instanceof PrivateKey) {
            return (PrivateKey) key;
        }
        return parser.parsePrivateKey(key);
    }

    public static PublicKey getPublicKey(Map<String, Object> key) {
        if (key == null) {
            return null;
        } else if (key instanceof PublicKey) {
            return (PublicKey) key;
        }
        return parser.parsePublicKey(key);
    }

    public static SymmetricKey getSymmetricKey(Map<String, Object> key) {
        if (key == null) {
            return null;
        } else if (key instanceof SymmetricKey) {
            return (SymmetricKey) key;
        }
        return parser.parseSymmetricKey(key);
    }

    /**
     *  Key Parser
     *  ~~~~~~~~~~
     */
    public interface Parser extends SymmetricKey.Parser, PublicKey.Parser, PrivateKey.Parser {

        /**
         *  Generate key with algorithm
         *
         * @param algorithm - key algorithm
         * @return SymmetricKey
         */
        SymmetricKey generateSymmetricKey(String algorithm);

        /**
         *  Generate key with algorithm
         *
         * @param algorithm - key algorithm
         * @return PrivateKey
         */
        PrivateKey generatePrivateKey(String algorithm);
    }

    // default parser
    public static Parser parser = new Parser() {
        @Override
        public SymmetricKey generateSymmetricKey(String algorithm) {
            throw new UnsupportedOperationException("implement me!");
        }

        @Override
        public PrivateKey generatePrivateKey(String algorithm) {
            throw new UnsupportedOperationException("implement me!");
        }

        @Override
        public PrivateKey parsePrivateKey(Map<String, Object> key) {
            throw new UnsupportedOperationException("implement me!");
        }

        @Override
        public PublicKey parsePublicKey(Map<String, Object> key) {
            throw new UnsupportedOperationException("implement me!");
        }

        @Override
        public SymmetricKey parseSymmetricKey(Map<String, Object> key) {
            throw new UnsupportedOperationException("implement me!");
        }
    };
}
