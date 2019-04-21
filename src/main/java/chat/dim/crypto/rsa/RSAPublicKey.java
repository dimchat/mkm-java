package chat.dim.crypto.rsa;

import chat.dim.crypto.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

public class RSAPublicKey extends PublicKey {

    private final java.security.interfaces.RSAPublicKey publicKey;

    public RSAPublicKey(RSAPublicKey key) {
        super(key);
        this.publicKey = key.publicKey;
    }

    public RSAPublicKey(HashMap<String, Object> dictionary) {
        super(dictionary);
        this.publicKey = getKey();
    }

    private int keySizeInBits() {
        Object size = dictionary.get("keySizeInBits");
        if (size == null) {
            dictionary.put("keySizeInBits", 1024);
            return 1024;
        }
        return (Integer) size;
    }

    private java.security.interfaces.RSAPublicKey getKey() {
        Object data = dictionary.get("data");
        if (data == null) {
            throw new AssertionError("public key data empty");
        } else {
            // parse PEM file content
            try {
                return (java.security.interfaces.RSAPublicKey) parse((String) data);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private java.security.PublicKey parse(String fileContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PEMFile pemFile = new PEMFile(fileContent);
        // public key
        byte[] publicKeyData = pemFile.publicKeyData;
        if (publicKeyData != null) {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyData);
            return factory.generatePublic(keySpec);
        }
        return null;
    }

    //-------- interfaces --------

    public byte[] encrypt(byte[] plaintext) {
        int blockSize = keySizeInBits() / 8 - 11;
        int length = plaintext.length;
        if (length > blockSize) {
            throw new AssertionError("RSA plaintext length error");
        }
        try {
            Cipher cipher = null;
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plaintext, 0, length);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean verify(byte[] data, byte[] signature) {
        System.out.println("override me!");
        // TODO: override me
        return false;
    }
}
