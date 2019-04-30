package chat.dim.crypto;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *  Symmetric Cryptography Key
 *
 *      keyInfo format: {
 *          algorithm: "AES",
 *          data     : "{BASE64_ENCODE}",
 *          ...
 *      }
 */
public abstract class SymmetricKey extends CryptographyKey {

    public static final String AES = "AES";
    public static final String DES = "DES";

    protected SymmetricKey(Map<String, Object> dictionary) {
        super(dictionary);
    }

    private static final byte[] promise = "Moky loves May Lee forever!".getBytes();

    public boolean equals(SymmetricKey key) {
        if (key == null) {
            return false;
        } else if (super.equals(key)) {
            return true;
        }
        byte[] cipherText = key.encrypt(promise);
        byte[] plainText = decrypt(cipherText);
        return Arrays.equals(plainText, promise);
    }

    //-------- Interfaces --------

    /**
     *  cipherText = encrypt(plainText, PW)
     *
     * @param plainText - plain data
     * @return cipherText
     */
    public abstract byte[] encrypt(byte[] plainText);

    /**
     *  plainText = decrypt(cipherText, PW);
     *
     * @param cipherText - encrypted data
     * @return plainText
     */
    public abstract byte[] decrypt(byte[] cipherText);

    //-------- Runtime --------

    private static Map<String, Class> symmetricKeyClasses = new HashMap<>();

    public static void register(String algorithm, Class clazz) {
        // TODO: check whether clazz is subclass of SymmetricKey
        symmetricKeyClasses.put(algorithm, clazz);
    }

    @SuppressWarnings("unchecked")
    private static SymmetricKey createInstance(Map<String, Object> dictionary) throws ClassNotFoundException {
        String algorithm = getAlgorithm(dictionary);
        Class clazz = symmetricKeyClasses.get(algorithm);
        if (clazz == null) {
            throw new ClassNotFoundException("unknown algorithm:" + algorithm);
        }
        try {
            Constructor constructor = clazz.getConstructor(Map.class);
            return (SymmetricKey) constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static SymmetricKey getInstance(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof SymmetricKey) {
            return (SymmetricKey) object;
        } else if (object instanceof Map) {
            return createInstance((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("unknown key:" + object);
        }
    }

    public static SymmetricKey create(String algorithm) throws ClassNotFoundException {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", algorithm);
        return createInstance(dictionary);
    }

    static {
        // AES
        register(AES, AESKey.class);
        // DES
        // ...
    }
}
