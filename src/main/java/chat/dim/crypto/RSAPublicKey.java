package chat.dim.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

class RSAPublicKey extends PublicKey {

    private final java.security.interfaces.RSAPublicKey publicKey;

    public RSAPublicKey(RSAPublicKey key) {
        super(key);
        this.publicKey = key.publicKey;
    }

    public RSAPublicKey(Map<String, Object> dictionary) throws NoSuchFieldException {
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

    private java.security.interfaces.RSAPublicKey getKey() throws NoSuchFieldException {
        Object data = dictionary.get("data");
        if (data == null) {
            throw new NoSuchFieldException("RSA public key data not found");
        }
        try {
            return (java.security.interfaces.RSAPublicKey) parse((String) data);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    private java.security.PublicKey parse(String fileContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PEMFile pemFile = new PEMFile(fileContent);
        byte[] publicKeyData = pemFile.publicKeyData;
        if (publicKeyData == null) {
            return null;
        }
        //PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(publicKeyData);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyData);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(keySpec);
    }

    //-------- interfaces --------

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length > (keySizeInBits() / 8 - 11)) {
            throw new InvalidParameterException("RSA plaintext length error:" + plaintext.length);
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plaintext);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verify(byte[] data, byte[] signature) {
        try {
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initVerify(publicKey);
            signer.update(data);
            return signer.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }
}
