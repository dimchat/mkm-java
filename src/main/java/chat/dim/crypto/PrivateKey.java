package chat.dim.crypto;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class PrivateKey extends CryptographyKey {

    public PrivateKey(Map<String, Object> dictionary) {
        super(dictionary);
    }

    public boolean equals(PrivateKey privateKey) {
        if (privateKey == null) {
            return false;
        } else if (super.equals(privateKey)) {
            return true;
        }
        PublicKey publicKey = getPublicKey();
        return publicKey != null && publicKey.matches(privateKey);
    }

    public PublicKey getPublicKey() {
        System.out.println("override me!");
        // TODO: override me
        return null;
    }

    public byte[] decrypt(byte[] cipherText) {
        System.out.println("override me!");
        // TODO: override me
        return null;
    }

    public byte[] sign(byte[] data) {
        System.out.println("override me!");
        // TODO: override me
        return null;
    }

    //-------- Runtime --------

    private static Map<String, Class> privateKeyClasses = new HashMap<>();

    public static void register(String algorithm, Class clazz) {
        // TODO: check whether clazz is subclass of PrivateKey
        privateKeyClasses.put(algorithm, clazz);
    }

    @SuppressWarnings("unchecked")
    private static PrivateKey createInstance(Map<String, Object> dictionary) throws ClassNotFoundException {
        String algorithm = getAlgorithm(dictionary);
        Class clazz = privateKeyClasses.get(algorithm);
        if (clazz == null) {
            throw new ClassNotFoundException("unknown algorithm:" + algorithm);
        }
        try {
            Constructor constructor = clazz.getConstructor(Map.class);
            return (PrivateKey) constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static PrivateKey getInstance(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof PrivateKey) {
            return (PrivateKey) object;
        } else if (object instanceof Map) {
            return createInstance((Map<String, Object>) object);
        } else if (object instanceof String) {
            return createInstance(Utils.jsonDecode((String) object));
        } else {
            throw new IllegalArgumentException("unknown key:" + object);
        }
    }

    static {
        // RSA
        register("RSA", RSAPrivateKey.class);
        // ECC
        // ...
    }
}
