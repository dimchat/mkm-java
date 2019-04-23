package chat.dim.crypto;

import chat.dim.crypto.aes.AESKey;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class SymmetricKey extends CryptographyKey {

    public SymmetricKey(SymmetricKey key) {
        super(key);
    }

    public SymmetricKey(HashMap<String, Object> dictionary) {
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

    //-------- Runtime begin --------

    private static HashMap<String, Class> symmetricKeyClasses = new HashMap<>();

    public static void register(String algorithm, Class clazz) {
        // TODO: check whether clazz is subclass of SymmetricKey
        symmetricKeyClasses.put(algorithm, clazz);
    }

    private static SymmetricKey createInstance(HashMap<String, Object> dictionary) throws ClassNotFoundException {
        String algorithm = getAlgorithm(dictionary);
        Class clazz = symmetricKeyClasses.get(algorithm);
        if (clazz == null) {
            throw new ClassNotFoundException("unknown algorithm:" + algorithm);
        }
        try {
            Constructor constructor = clazz.getConstructor(HashMap.class);
            return (SymmetricKey) constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SymmetricKey create(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        }
        if (object instanceof SymmetricKey) {
            return (SymmetricKey) object;
        } else if (object instanceof HashMap) {
            return createInstance((HashMap<String, Object>) object);
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

    //-------- Runtime end --------

    public static void main(String args[]) throws UnsupportedEncodingException, ClassNotFoundException {
        HashMap<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", "AES");
        dictionary.put("data", "C2+xGizLL1G1+z9QLPYNdp/bPP/seDvNw45SXPAvQqk=");
        dictionary.put("iv", "SxPwi6u4+ZLXLdAFJezvSQ==");

        SymmetricKey key = SymmetricKey.create(dictionary);
        System.out.println("key:" + key);

        String text = "moky";
        byte[] plaintext = text.getBytes("UTF-8");
        byte[] cipherText = key.encrypt(plaintext);
        System.out.println("RSA encrypt(\"" + text + "\") = " + Utils.hexEncode(cipherText));

        byte[] data = key.decrypt(cipherText);
        String decrypt = new String(data);
        System.out.println("decrypt to " + decrypt);

        if (Utils.base64Encode(cipherText).equals("0xtbqZN6x2aWTZn0DpCoCA==")) {
            System.out.println("!!! AES encrypt OK");
        } else {
            throw new ArithmeticException("!!! AES encrypt error");
        }

        // random key
        dictionary.remove("data");
        dictionary.remove("iv");
        key = SymmetricKey.create(dictionary);
        System.out.println("key:" + key);

        cipherText = key.encrypt(plaintext);
        System.out.println("RSA encrypt(\"" + text + "\") = " + Utils.hexEncode(cipherText));

        data = key.decrypt(cipherText);
        decrypt = new String(data);
        System.out.println("decrypt to " + decrypt);

        if (decrypt.equals(text)) {
            System.out.println("!!! AES encrypt OK");
        } else {
            throw new ArithmeticException("!!! AES encrypt error");
        }

//        text = "XX5qfromb3R078VVK7LwVA==";
//        cipherText = Utils.base64Decode(text);
//        plaintext = key.decrypt(cipherText);
//        System.out.println(text + " -> " + new String(plaintext));
//        plaintext = key.decrypt(cipherText);
//        System.out.println(text + " -> " + new String(plaintext));
    }
}
