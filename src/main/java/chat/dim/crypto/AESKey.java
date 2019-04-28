package chat.dim.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;

class AESKey extends SymmetricKey {

    private final Cipher cipher;

    private final SecretKeySpec keySpec;
    private final IvParameterSpec ivSpec;

    public AESKey(Map<String, Object> dictionary) throws NoSuchPaddingException, NoSuchAlgorithmException {
        super(dictionary);
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        keySpec = new SecretKeySpec(getKeyData(), "AES");
        ivSpec = new IvParameterSpec(getInitVector());
    }

    private int getKeySize() {
        Object size = dictionary.get("keySize");
        if (size == null) {
            dictionary.put("keySize", 32);
            return 32;
        }
        return (Integer) size;
    }

    private int getBlockSize() {
        return cipher.getBlockSize();
    }

    private byte[] randomData(int size) {
        Random random = new Random();
        byte[] buffer = new byte[size];
        random.nextBytes(buffer);
        return buffer;
    }

    private byte[] zeroData(int size) {
        return new byte[size];
    }

    private byte[] getKeyData() {
        Object data = dictionary.get("data");
        if (data != null) {
            return Utils.base64Decode((String) data);
        }

        // random key data
        int keySize = getKeySize();
        byte[] pw = randomData(keySize);
        dictionary.put("data", Utils.base64Encode(pw));

        // random initialization vector
        int blockSize = getBlockSize();
        byte[] iv = randomData(blockSize);
        dictionary.put("iv", Utils.base64Encode(iv));

        return pw;
    }

    private byte[] getInitVector() {
        Object iv = dictionary.get("iv");
        if (iv != null) {
            return Utils.base64Decode((String) iv);
        }
        // zero iv
        int blockSize = getBlockSize();
        byte[] zeros = zeroData(blockSize);
        dictionary.put("iv", Utils.base64Encode(zeros));
        return zeros;
    }

    //-------- interfaces --------

    public byte[] encrypt(byte[] plainText) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(plainText);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decrypt(byte[] cipherText) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(cipherText);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
