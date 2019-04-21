package chat.dim.crypto.aes;

import chat.dim.crypto.SymmetricKey;
import chat.dim.crypto.Utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;

public class AESKey extends SymmetricKey {

    private final SecretKeySpec keySpec;
    private final IvParameterSpec ivSpec;

    public AESKey(AESKey key) {
        super(key);
        this.keySpec = key.keySpec;
        this.ivSpec = key.ivSpec;
    }

    public AESKey(HashMap<String, Object> dictionary) {
        super(dictionary);
        keySpec = new SecretKeySpec(getData(), "AES");
        ivSpec = new IvParameterSpec(getInitVector());
    }

    private int keySize() {
        Object size = dictionary.get("keySize");
        if (size == null) {
            dictionary.put("keySize", 32);
            return 32;
        }
        return (Integer) size;
    }

    private byte[] randomData(int size) {
        Random random = new Random();
        byte[] buffer = new byte[size];
        random.nextBytes(buffer);
        return buffer;
    }

    private byte[] getData() {
        Object data = dictionary.get("data");
        if (data != null) {
            return Utils.base64Decode((String) data);
        }

        // random password
        int size = keySize();
        byte[] pw = randomData(size);
        dictionary.put("data", Utils.base64Encode(pw));

        // random initialization vector
        int blockSize = 16;
        byte[] iv = randomData(blockSize);
        dictionary.put("iv", Utils.base64Encode(iv));

        return pw;
    }

    private byte[] getInitVector() {
        Object iv = dictionary.get("iv");
        if (iv != null) {
            return Utils.base64Decode((String) iv);
        }
        return null;
    }

    //-------- interfaces --------

    public byte[] encrypt(byte[] plaintext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(plaintext);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | InvalidAlgorithmParameterException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decrypt(byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(cipherText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | InvalidAlgorithmParameterException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
