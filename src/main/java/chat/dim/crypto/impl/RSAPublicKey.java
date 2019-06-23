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
package chat.dim.crypto.impl;

import chat.dim.format.PEM;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

/**
 *  RSA Public Key
 *
 *      keyInfo format: {
 *          algorithm: "RSA",
 *          data: "..."       // base64
 *      }
 */
final class RSAPublicKey extends PublicKeyImpl {

    private final java.security.interfaces.RSAPublicKey publicKey;

    public RSAPublicKey(Map<String, Object> dictionary)
            throws NoSuchFieldException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        super(dictionary);
        publicKey = getKey();
    }

    private int keySize() {
        // TODO: get from key

        Object size = dictionary.get("keySize");
        if (size == null) {
            return 1024 / 8; // 128
        } else  {
            return (int) size;
        }
    }

    private java.security.interfaces.RSAPublicKey getKey()
            throws NoSuchFieldException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        Object data = dictionary.get("data");
        if (data == null) {
            throw new NoSuchFieldException("RSA public key data not found");
        }
        return parse((String) data);
    }

    private static java.security.interfaces.RSAPublicKey parse(String fileContent)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        PEM pemFile = new PEM(fileContent);
        byte[] publicKeyData = pemFile.publicKeyData;
        if (publicKeyData == null) {
            return null;
        }
        // X.509
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyData);
        KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
        return (java.security.interfaces.RSAPublicKey) factory.generatePublic(keySpec);
    }

    @Override
    public byte[] getData() {
        return publicKey == null ? null : publicKey.getEncoded();
    }

    @Override
    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length > (keySize() - 11)) {
            throw new InvalidParameterException("RSA plain text length error: " + plaintext.length);
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plaintext);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean verify(byte[] data, byte[] signature) {
        try {
            Signature signer = Signature.getInstance("SHA256withRSA", "BC");
            signer.initVerify(publicKey);
            signer.update(data);
            return signer.verify(signature);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
}
