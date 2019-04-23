package chat.dim.crypto.rsa;

import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;

public class RSAPrivateKey extends PrivateKey {

    private final java.security.interfaces.RSAPrivateKey privateKey;
    private final java.security.interfaces.RSAPublicKey publicKey;

    public RSAPrivateKey(RSAPrivateKey key) {
        super(key);
        this.privateKey = key.privateKey;
        this.publicKey = key.publicKey;
    }

    public RSAPrivateKey(HashMap<String, Object> dictionary) {
        super(dictionary);
        KeyPair keyPair = getKeyPair();
        if (keyPair == null) {
            privateKey = null;
            publicKey = null;
            // crypto key data
            data = null;
        } else {
            privateKey = (java.security.interfaces.RSAPrivateKey) keyPair.getPrivate();
            publicKey = (java.security.interfaces.RSAPublicKey) keyPair.getPublic();
            // crypto key data
            data = privateKey.getEncoded();
        }
    }

    private KeyPair getKeyPair() {
        KeyPair keyPair = null;
        Object data = dictionary.get("data");
        if (data == null) {
            // generate key
            int bits = keySizeInBits();
            try {
                keyPair = generate(bits);
                updateData(keyPair);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            // parse PEM file content
            try {
                keyPair = parse((String) data);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
        return keyPair;
    }

    private void updateData(KeyPair keyPair) {
        PEMFile pemFile = new PEMFile((java.security.interfaces.RSAPrivateKey) keyPair.getPrivate());
        dictionary.put("data", pemFile.toString());
    }

    private int keySizeInBits() {
        Object size = dictionary.get("keySizeInBits");
        if (size == null) {
            dictionary.put("keySizeInBits", 1024);
            return 1024;
        }
        return (Integer) size;
    }

    private KeyPair generate(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keySize);
        return generator.generateKeyPair();
    }

    private KeyPair parse(String fileContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        java.security.PublicKey publicKey;
        java.security.PrivateKey privateKey;
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PEMFile pemFile = new PEMFile(fileContent);
        // private key
        byte[] privateKeyData = pemFile.privateKeyData;
        if (privateKeyData != null) {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyData);
            privateKey = factory.generatePrivate(keySpec);
        } else {
            privateKey = null;
        }
        // public key
        byte[] publicKeyData = pemFile.publicKeyData;
        if (publicKeyData != null) {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(publicKeyData);
            publicKey = factory.generatePublic(keySpec);
        } else {
            publicKey = null;
        }
        return new KeyPair(publicKey, privateKey);
    }

    //-------- interfaces --------

    public PublicKey getPublicKey() {
        if (publicKey == null && privateKey != null) {
            // TODO: get public from private key
        }
        if (publicKey == null) {
            return null;
        }
        PEMFile pemFile = new PEMFile(publicKey);
        HashMap<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", "RSA");
        dictionary.put("keySizeInBits", keySizeInBits());
        dictionary.put("data", pemFile.toString());
        try {
            return PublicKey.create(dictionary);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decrypt(byte[] cipherText) {
        if (cipherText.length != (keySizeInBits() / 8)) {
            throw new InvalidParameterException("RSA cipher text length error:" + cipherText.length);
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(cipherText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] sign(byte[] data) {
        try {
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(privateKey);
            signer.update(data);
            return signer.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }
}
