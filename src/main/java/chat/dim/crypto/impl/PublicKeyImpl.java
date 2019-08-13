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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
import chat.dim.crypto.plugins.RSAPublicKey;

/**
 *  Asymmetric Cryptography Public Key
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  key data format: {
 *      algorithm : "RSA", // "ECC", ...
 *      data      : "{BASE64_ENCODE}",
 *      ...
 *  }
 */
public abstract class PublicKeyImpl extends CryptographyKeyImpl implements PublicKey {

    protected PublicKeyImpl(Map<String, Object> dictionary) {
        super(dictionary);
    }

    private static final byte[] promise = "Moky loves May Lee forever!".getBytes();

    @Override
    public boolean matches(PrivateKey privateKey) {
        if (privateKey == null) {
            return false;
        }
        // 1. if the SK has the same public key, return true
        PublicKey publicKey = privateKey.getPublicKey();
        if (publicKey != null && publicKey.equals(this)) {
            return true;
        }
        // 2. try to verify the SK's signature
        byte[] signature = privateKey.sign(promise);
        return verify(promise, signature);
    }

    //-------- Runtime --------

    private static Map<String, Class> publicKeyClasses = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void register(String algorithm, Class clazz) {
        // check whether clazz is subclass of PublicKey
        clazz = clazz.asSubclass(PublicKey.class);
        publicKeyClasses.put(algorithm, clazz);
    }

    @SuppressWarnings("unchecked")
    public static PublicKey getInstance(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof PublicKey) {
            return (PublicKey) object;
        }
        assert object instanceof Map;
        Map<String, Object> dictionary = (Map<String, Object>) object;
        // get subclass by key algorithm
        String algorithm = (String) dictionary.get("algorithm");
        Class clazz = publicKeyClasses.get(algorithm);
        if (clazz == null) {
            throw new ClassNotFoundException("algorithm not support: " + algorithm);
        }
        try {
            Constructor constructor = clazz.getConstructor(Map.class);
            return (PublicKey) constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    static {
        // RSA
        register(RSA, RSAPublicKey.class); // default
        register("SHA256withRSA", RSAPublicKey.class);
        register("RSA/ECB/PKCS1Padding", RSAPublicKey.class);
        // ECC
        // ...
    }
}
