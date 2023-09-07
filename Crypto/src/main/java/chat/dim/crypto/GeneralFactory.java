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

public class GeneralFactory {

    private final Map<String, SymmetricKey.Factory> symmetricKeyFactories = new HashMap<>();

    private final Map<String, PublicKey.Factory> publicKeyFactories = new HashMap<>();

    private final Map<String, PrivateKey.Factory> privateKeyFactories = new HashMap<>();

    // sample data for checking keys
    private static final byte[] promise = "Moky loves May Lee forever!".getBytes();

    public boolean matches(SignKey sKey, VerifyKey pKey) {
        // verify with signature
        byte[] signature = sKey.sign(promise);
        return pKey.verify(promise, signature);
    }
    public boolean matches(EncryptKey pKey, DecryptKey sKey) {
        // check by encryption
        Map<String, String> extra = new HashMap<>();
        byte[] ciphertext = pKey.encrypt(promise, extra);
        byte[] plaintext = sKey.decrypt(ciphertext, extra);
        return Arrays.equals(plaintext, promise);
    }

    public String getAlgorithm(Map<?, ?> key) {
        return (String) key.get("algorithm");
    }

    //
    //  SymmetricKey
    //

    public void setSymmetricKeyFactory(String algorithm, SymmetricKey.Factory factory) {
        symmetricKeyFactories.put(algorithm, factory);
    }

    public SymmetricKey.Factory getSymmetricKeyFactory(String algorithm) {
        return symmetricKeyFactories.get(algorithm);
    }

    public SymmetricKey generateSymmetricKey(String algorithm) {
        SymmetricKey.Factory factory = getSymmetricKeyFactory(algorithm);
        assert factory != null : "key algorithm not support: " + algorithm;
        return factory.generateSymmetricKey();
    }

    public SymmetricKey parseSymmetricKey(Object key) {
        if (key == null) {
            return null;
        } else if (key instanceof SymmetricKey) {
            return (SymmetricKey) key;
        }
        Map<String, Object> info = Wrapper.getMap(key);
        if (info == null) {
            assert false : "key error: " + key;
            return null;
        }
        String algorithm = getAlgorithm(info);
        assert algorithm != null : "failed to get algorithm name from key: " + key;
        SymmetricKey.Factory factory = getSymmetricKeyFactory(algorithm);
        if (factory == null) {
            factory = getSymmetricKeyFactory("*");  // unknown
            assert factory != null : "cannot parse key: " + key;
        }
        return factory.parseSymmetricKey(info);
    }

    //
    //  PrivateKey
    //

    public void setPrivateKeyFactory(String algorithm, PrivateKey.Factory factory) {
        privateKeyFactories.put(algorithm, factory);
    }

    public PrivateKey.Factory getPrivateKeyFactory(String algorithm) {
        return privateKeyFactories.get(algorithm);
    }

    public PrivateKey generatePrivateKey(String algorithm) {
        PrivateKey.Factory factory = getPrivateKeyFactory(algorithm);
        assert factory != null : "key algorithm not support: " + algorithm;
        return factory.generatePrivateKey();
    }

    public PrivateKey parsePrivateKey(Object key) {
        if (key == null) {
            return null;
        } else if (key instanceof PrivateKey) {
            return (PrivateKey) key;
        }
        Map<String, Object> info = Wrapper.getMap(key);
        if (info == null) {
            assert false : "key error: " + key;
            return null;
        }
        String algorithm = getAlgorithm(info);
        assert algorithm != null : "failed to get algorithm name from key: " + key;
        PrivateKey.Factory factory = getPrivateKeyFactory(algorithm);
        if (factory == null) {
            factory = getPrivateKeyFactory("*");  // unknown
            assert factory != null : "cannot parse key: " + key;
        }
        return factory.parsePrivateKey(info);
    }

    //
    //  PublicKey
    //

    public void setPublicKeyFactory(String algorithm, PublicKey.Factory factory) {
        publicKeyFactories.put(algorithm, factory);
    }

    public PublicKey.Factory getPublicKeyFactory(String algorithm) {
        return publicKeyFactories.get(algorithm);
    }

    public PublicKey parsePublicKey(Object key) {
        if (key == null) {
            return null;
        } else if (key instanceof PublicKey) {
            return (PublicKey) key;
        }
        Map<String, Object> info = Wrapper.getMap(key);
        if (info == null) {
            assert false : "key error: " + key;
            return null;
        }
        String algorithm = getAlgorithm(info);
        assert algorithm != null : "failed to get algorithm name from key: " + key;
        PublicKey.Factory factory = getPublicKeyFactory(algorithm);
        if (factory == null) {
            factory = getPublicKeyFactory("*");  // unknown
            assert factory != null : "cannot parse key: " + key;
        }
        return factory.parsePublicKey(info);
    }
}
