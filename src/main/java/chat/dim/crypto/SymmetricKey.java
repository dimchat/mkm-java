package chat.dim.crypto;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SymmetricKey extends CryptographyKey {

    public SymmetricKey(SymmetricKey key) {
        super(key);
    }

    public SymmetricKey(Map<String, Object> dictionary) {
        super(dictionary);
    }

    private static final byte[] promise = "Moky loves May Lee forever!".getBytes();

    public boolean equals(SymmetricKey key) {
        if (key == null) {
            return false;
        } else if (super.equals(key)) {
            return true;
        }
        byte[] ciphertext = key.encrypt(promise);
        byte[] plaintext = decrypt(ciphertext);
        return Arrays.equals(plaintext, promise);
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
        } else if (object instanceof String) {
            return createInstance(Utils.jsonDecode((String) object));
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
