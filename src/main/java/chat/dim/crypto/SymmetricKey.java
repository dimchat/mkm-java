package chat.dim.crypto;

import chat.dim.crypto.aes.AESKey;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class SymmetricKey extends CryptographyKey {

    public SymmetricKey(SymmetricKey key) {
        super(key);
    }

    public SymmetricKey(Map<String, Object> dictionary) {
        super(dictionary);
    }

    public byte[] encrypt(byte[] plaintext) {
        System.out.println("override me!");
        // TODO: override me
        return null;
    }

    public byte[] decrypt(byte[] ciphertext) {
        System.out.println("override me!");
        // TODO: override me
        return null;
    }

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

    static {
        // AES
        register("AES", AESKey.class);
        // DES
        // ...
    }
}
