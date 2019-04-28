package chat.dim.crypto;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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

    public abstract byte[] encrypt(byte[] plainText);

    public abstract boolean verify(byte[] data, byte[] signature);

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
        register(RSA, RSAPublicKey.class);
        // ECC
        // ...
    }
}
