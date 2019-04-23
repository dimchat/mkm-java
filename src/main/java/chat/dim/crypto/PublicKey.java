package chat.dim.crypto;

import chat.dim.crypto.rsa.RSAPublicKey;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class PublicKey extends CryptographyKey {

    public PublicKey(PublicKey key) {
        super(key);
    }

    public PublicKey(HashMap<String, Object> dictionary) {
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

    //-------- Runtime begin --------

    private static HashMap<String, Class> publicKeyClasses = new HashMap<>();

    public static void register(String algorithm, Class clazz) {
        // TODO: check whether clazz is subclass of PublicKey
        publicKeyClasses.put(algorithm, clazz);
    }

    private static PublicKey createInstance(HashMap<String, Object> dictionary) throws ClassNotFoundException {
        String algorithm = getAlgorithm(dictionary);
        Class clazz = publicKeyClasses.get(algorithm);
        if (clazz == null) {
            throw new ClassNotFoundException("unknown algorithm:" + algorithm);
        }
        try {
            Constructor constructor = clazz.getConstructor(HashMap.class);
            return (PublicKey) constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PublicKey create(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        }
        if (object instanceof PublicKey) {
            return (PublicKey) object;
        } else if (object instanceof HashMap) {
            return createInstance((HashMap<String, Object>) object);
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

    //-------- Runtime end --------

    public static void main(String args[]) throws ClassNotFoundException {
        HashMap<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", "RSA");
        dictionary.put("data", "-----BEGIN PUBLIC KEY-----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCI8jKup683D4Gxa0dJomGMqHhd3bcHr7NObJuglNLvYir9PFsfs/mNB/K6jN+R+O6hpyCIiKARk0zxxfuzzLdZhXWmqcvy4f95cJAG5aYOtv8RACwRo/b9/NaDuHnpBW7soArZDS8RqTI1lYH5v2tZqMIdhoC5DAUyKOHFcGxiGQIDAQAB\n" +
                "-----END PUBLIC KEY-----");

        PublicKey key = PublicKey.create(dictionary);
        System.out.println("key:" + key);
    }
}
