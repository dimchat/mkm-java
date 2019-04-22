package chat.dim.crypto;

import chat.dim.crypto.rsa.RSAPrivateKey;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class PrivateKey extends CryptographyKey {

    public PrivateKey(PrivateKey key) {
        super(key);
    }

    public PrivateKey(HashMap<String, Object> dictionary) {
        super(dictionary);
    }

    public PublicKey getPublicKey() {
        System.out.println("override me!");
        // TODO: override me
        return null;
    }

    public byte[] decrypt(byte[] ciphertext) {
        System.out.println("override me!");
        // TODO: override me
        return null;
    }

    public byte[] sign(byte[] data) {
        System.out.println("override me!");
        // TODO: override me
        return null;
    }

    //-------- Runtime begin --------

    private static HashMap<String, Class> privateKeyClasses = new HashMap<>();

    public static void register(String algorithm, Class keyClass) {
        // TODO: check whether is subclass of PrivateKey
        privateKeyClasses.put(algorithm, keyClass);
    }

    private static PrivateKey createInstance(HashMap<String, Object> dictionary) {
        String algorithm = getAlgorithm(dictionary);
        Class clazz = privateKeyClasses.get(algorithm);
        if (clazz == null) {
            System.out.println("unknown algorithm:" + algorithm);
            return null;
        }
        try {
            Constructor constructor = clazz.getConstructor(HashMap.class);
            return (PrivateKey) constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey create(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof PrivateKey) {
            return (PrivateKey) object;
        }
        Class clazz = object.getClass();
        if (clazz.equals(HashMap.class)) {
            HashMap<String, Object> dictionary = (HashMap<String, Object>) object;
            return createInstance(dictionary);
        }
        throw new AssertionError("unknown key:" + object);
    }

    static {
        // RSA
        register("RSA", RSAPrivateKey.class);
        // ECC
        // ...
    }

    //-------- Runtime end --------

    public static void main(String args[]) throws UnsupportedEncodingException {
        HashMap<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", "RSA");

        PrivateKey sk = PrivateKey.create(dictionary);
        System.out.println("RSA private key:" + sk);

        PublicKey pk = sk.getPublicKey();
        System.out.println("RSA public key:" + pk);

        String text = "moky";
        byte[] plaintext = text.getBytes("UTF-8");
        byte[] cipherText = pk.encrypt(plaintext);
        System.out.println("RSA encrypt(\"" + text + "\") = " + Utils.hexEncode(cipherText));

        byte[] data = sk.decrypt(cipherText);
        String decrypt = new String(data);
        System.out.println("decrypt to " + decrypt);

        if (decrypt.equals(text)) {
            System.out.println("!!! RSA en/decrypt OK");
        } else {
            throw new AssertionError("!!! RSA en/decrypt error");
        }

        byte[] signature = sk.sign(plaintext);
        System.out.println("signature(\"" + text + "\") = " + Utils.hexEncode(signature));
        if (pk.verify(plaintext, signature)) {
            System.out.println("!!! RSA signature OK");
        } else {
            throw new AssertionError("!!! RSA signature error");
        }
    }
}
