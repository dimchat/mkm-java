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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 *  Asymmetric Cryptography Key
 *
 *      keyInfo format: {
 *          algorithm: "RSA", // ECC, ...
 *          data     : "{BASE64_ENCODE}",
 *          ...
 *      }
 */
public abstract class PublicKey extends CryptographyKey {

    public static final String RSA = "RSA";
    public static final String ECC = "ECC";

    protected PublicKey(Map<String, Object> dictionary) {
        super(dictionary);
    }

    private static final byte[] promise = "Moky loves May Lee forever!".getBytes();

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

    //-------- Interfaces --------

    /**
     *  ciphertext = encrypt(plaintext, PK)
     *
     * @param plaintext - data to be encrypted
     * @return ciphertext
     */
    public abstract byte[] encrypt(byte[] plaintext);

    /**
     *  OK = verify(data, signature, PK)
     *
     * @param data - data
     * @param signature - signature of data
     * @return true on signature matched
     */
    public abstract boolean verify(byte[] data, byte[] signature);

    //-------- Runtime --------

    private static Map<String, Class> publicKeyClasses = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void register(String algorithm, Class clazz) {
        // check whether clazz is subclass of PublicKey
        clazz = clazz.asSubclass(PublicKey.class);
        publicKeyClasses.put(algorithm, clazz);
    }

    @SuppressWarnings("unchecked")
    private static PublicKey createInstance(Map<String, Object> dictionary) throws ClassNotFoundException {
        String algorithm = (String) dictionary.get("algorithm");
        Class clazz = publicKeyClasses.get(algorithm);
        if (clazz == null) {
            throw new ClassNotFoundException("unknown algorithm:" + algorithm);
        }
        try {
            Constructor constructor = clazz.getConstructor(Map.class);
            return (PublicKey) constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static PublicKey getInstance(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof PublicKey) {
            return (PublicKey) object;
        } else if (object instanceof Map) {
            return createInstance((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("unknown key:" + object);
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
