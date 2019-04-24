package chat.dim.crypto;

import chat.dim.crypto.rsa.RSAPublicKey;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class PublicKey extends CryptographyKey {

    public PublicKey(PublicKey key) {
        super(key);
    }

    public PublicKey(Map<String, Object> dictionary) {
        super(dictionary);
    }

    public byte[] encrypt(byte[] plaintext) {
        System.out.println("override me!");
        // TODO: override me
        return null;
    }

    public boolean verify(byte[] data, byte[] signature) {
        System.out.println("override me!");
        // TODO: override me
        return false;
    }

    //-------- Runtime --------

    private static Map<String, Class> publicKeyClasses = new HashMap<>();

    public static void register(String algorithm, Class clazz) {
        // TODO: check whether clazz is subclass of PublicKey
        publicKeyClasses.put(algorithm, clazz);
    }

    @SuppressWarnings("unchecked")
    private static PublicKey createInstance(Map<String, Object> dictionary) throws ClassNotFoundException {
        String algorithm = getAlgorithm(dictionary);
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
        register("RSA", RSAPublicKey.class);
        // ECC
        // ...
    }
}
