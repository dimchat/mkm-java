package chat.dim.crypto;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class PrivateKey extends CryptographyKey {

    public static final String RSA = "RSA";
    public static final String ECC = "ECC";

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

    //-------- Interfaces --------

    public abstract PublicKey getPublicKey();

    public abstract byte[] decrypt(byte[] cipherText);

    public abstract byte[] sign(byte[] data);

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
        } else {
            throw new IllegalArgumentException("unknown key:" + object);
        }
    }

    public static PrivateKey create(String algorithm) throws ClassNotFoundException {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", algorithm);
        return createInstance(dictionary);
    }

    static {
        // RSA
        register(RSA, RSAPrivateKey.class);
        // ECC
        // ...
    }
}
