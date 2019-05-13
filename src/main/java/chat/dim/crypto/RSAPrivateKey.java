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
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 *  RSA Private Key
 *
 *      keyInfo format: {
 *          algorithm    : "RSA",
 *          keySizeInBits: 1024, // optional
 *          data         : "..." // base64_encode()
 *      }
 */
final class RSAPrivateKey extends PrivateKey {

    private final java.security.interfaces.RSAPrivateKey privateKey;
    private final java.security.interfaces.RSAPublicKey publicKey;

    public RSAPrivateKey(Map<String, Object> dictionary) {
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
        // -----BEGIN PUBLIC KEY-----
        PEMFile pkFile = new PEMFile((java.security.interfaces.RSAPublicKey) keyPair.getPublic());
        // -----END PUBLIC KEY-----
        // -----BEGIN RSA PRIVATE KEY-----
        PEMFile skFile = new PEMFile((java.security.interfaces.RSAPrivateKey) keyPair.getPrivate());
        // -----END RSA PRIVATE KEY-----
        dictionary.put("data", pkFile.toString() + "\n" + skFile.toString());
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
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", "RSA");
        dictionary.put("keySizeInBits", keySizeInBits());
        dictionary.put("data", pemFile.toString());
        try {
            return PublicKey.getInstance(dictionary);
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
