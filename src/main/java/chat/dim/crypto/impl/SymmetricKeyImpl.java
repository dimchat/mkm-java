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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import chat.dim.crypto.SymmetricKey;
import chat.dim.crypto.plugins.AESKey;

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
public abstract class SymmetricKeyImpl extends CryptographyKeyImpl implements SymmetricKey {

    protected SymmetricKeyImpl(Map<String, Object> dictionary) {
        super(dictionary);
    }

    private static final byte[] promise = "Moky loves May Lee forever!".getBytes();

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            // same dictionary
            return true;
        } else if (other instanceof SymmetricKey) {
            // check by encryption
            SymmetricKey key = (SymmetricKey) other;
            byte[] ciphertext = key.encrypt(promise);
            byte[] plaintext = decrypt(ciphertext);
            return Arrays.equals(plaintext, promise);
        } else {
            // null or unknown object
            return false;
        }
    }

    //-------- Runtime --------

    private static Map<String, Class> symmetricKeyClasses = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void register(String algorithm, Class clazz) {
        // check whether clazz is subclass of SymmetricKey
        clazz = clazz.asSubclass(SymmetricKey.class);
        symmetricKeyClasses.put(algorithm, clazz);
    }

    @SuppressWarnings("unchecked")
    public static SymmetricKey getInstance(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof SymmetricKey) {
            return (SymmetricKey) object;
        }
        assert object instanceof Map;
        Map<String, Object> dictionary = (Map<String, Object>) object;
        // get subclass by key algorithm
        String algorithm = getAlgorithm(dictionary);
        Class clazz = symmetricKeyClasses.get(algorithm);
        if (clazz == null) {
            throw new ClassNotFoundException("algorithm not support: " + algorithm);
        }
        try {
            Constructor constructor = clazz.getConstructor(Map.class);
            return (SymmetricKey) constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SymmetricKey generate(String algorithm) throws ClassNotFoundException {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", algorithm);
        return getInstance(dictionary);
    }

    static {
        // AES
        register(AES, AESKey.class); // default
        register("AES/CBC/PKCS7Padding", AESKey.class);
        // DES
        // ...
    }
}
