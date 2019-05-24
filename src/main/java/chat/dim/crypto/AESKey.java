/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Albert Moky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ==============================================================================
 */
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

/**
 *  AES Key
 *
 *      keyInfo format: {
 *          algorithm: "AES",
 *          keySize  : 32,                // optional
 *          data     : "{BASE64_ENCODE}}" // password data
 *          iv       : "{BASE64_ENCODE}", // initialization vector
 *      }
 */
final class AESKey extends SymmetricKey {

    private final Cipher cipher;

    private final SecretKeySpec keySpec;
    private final IvParameterSpec ivSpec;

    public AESKey(Map<String, Object> dictionary) throws NoSuchPaddingException, NoSuchAlgorithmException {
        super(dictionary);
        // TODO: check algorithm parameters
        // 1. check mode = 'CBC'
        // 2. check padding = 'PKCS7Padding'

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

    private static byte[] randomData(int size) {
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
            return Base64.decode((String) data);
        }

        // random key data
        int keySize = getKeySize();
        byte[] pw = randomData(keySize);
        dictionary.put("data", Base64.encode(pw));

        // random initialization vector
        int blockSize = getBlockSize();
        byte[] iv = randomData(blockSize);
        dictionary.put("iv", Base64.encode(iv));

        return pw;
    }

    private byte[] getInitVector() {
        Object iv = dictionary.get("iv");
        if (iv != null) {
            return Base64.decode((String) iv);
        }
        // zero iv
        int blockSize = getBlockSize();
        byte[] zeros = zeroData(blockSize);
        dictionary.put("iv", Base64.encode(zeros));
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
