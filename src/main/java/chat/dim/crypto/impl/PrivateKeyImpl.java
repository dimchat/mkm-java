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
package chat.dim.crypto.impl;

import java.util.HashMap;
import java.util.Map;

import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
import chat.dim.crypto.plugins.RSAPrivateKey;

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
public abstract class PrivateKeyImpl extends CryptographyKeyImpl implements PrivateKey {

    protected PrivateKeyImpl(Map<String, Object> dictionary) {
        super(dictionary);
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            // same dictionary
            return true;
        } else if (other instanceof PrivateKey) {
            // check by encryption
            PublicKey publicKey = getPublicKey();
            if (publicKey == null) {
                throw new NullPointerException("failed to get public key: " + this);
            }
            PrivateKey key = (PrivateKey) other;
            return publicKey.matches(key);
        } else {
            // null or unknown object
            return false;
        }
    }

    //-------- Runtime --------

    private static Map<String, Class> privateKeyClasses = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void register(String algorithm, Class clazz) {
        // check whether clazz is subclass of PrivateKey
        assert PrivateKey.class.isAssignableFrom(clazz); // asSubclass
        privateKeyClasses.put(algorithm, clazz);
    }

    private static Class keyClass(Map<String, Object> dictionary) {
        // get subclass by key algorithm
        String algorithm = getAlgorithm(dictionary);
        return privateKeyClasses.get(algorithm);
    }

    @SuppressWarnings("unchecked")
    public static PrivateKey getInstance(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof PrivateKey) {
            return (PrivateKey) object;
        }
        assert object instanceof Map;
        Map<String, Object> dictionary = (Map<String, Object>) object;
        Class clazz = keyClass(dictionary);
        if (clazz != null) {
            // create instance by subclass (with algorithm)
            return (PrivateKey) createInstance(clazz, dictionary);
        }
        throw new ClassNotFoundException("key error: " + dictionary);
    }

    public static PrivateKey generate(String algorithm) throws ClassNotFoundException {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", algorithm);
        return getInstance(dictionary);
    }

    static {
        // RSA
        register(RSA, RSAPrivateKey.class); // default
        register("SHA256withRSA", RSAPrivateKey.class);
        register("RSA/ECB/PKCS1Padding", RSAPrivateKey.class);
        // ECC
        // ...
    }
}
