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
package chat.dim.crypto.plugins;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import chat.dim.crypto.PublicKey;
import chat.dim.crypto.impl.PrivateKeyImpl;
import chat.dim.format.PEM;

/**
 *  RSA Private Key
 *
 *      keyInfo format: {
 *          algorithm    : "RSA",
 *          keySizeInBits: 1024, // optional
 *          data         : "..." // base64_encode()
 *      }
 */
public final class RSAPrivateKey extends PrivateKeyImpl {

    private final java.security.interfaces.RSAPrivateKey privateKey;
    private final java.security.interfaces.RSAPublicKey publicKey;

    public RSAPrivateKey(Map<String, Object> dictionary)
            throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
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

    private int keySize() {
        // TODO: get from key

        Object size = dictionary.get("keySize");
        if (size == null) {
            return 1024 / 8; // 128
        } else  {
            return (int) size;
        }
    }

    private KeyPair getKeyPair()
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchProviderException {
        Object data = dictionary.get("data");
        if (data == null) {
            // generate key
            int bits = keySize() * 8;
            return generate(bits);
        } else {
            // parse PEM file content
            return parse((String) data);
        }
    }

    private KeyPair generate(int sizeInBits)
            throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        KeyPairGenerator generator;
        try {
            generator = KeyPairGenerator.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
            generator = KeyPairGenerator.getInstance("RSA");
        }
        generator.initialize(sizeInBits);
        KeyPair keyPair = generator.generateKeyPair();

        // -----BEGIN PUBLIC KEY-----
        PEM pkFile = new PEM((java.security.interfaces.RSAPublicKey) keyPair.getPublic());
        // -----END PUBLIC KEY-----

        // -----BEGIN RSA PRIVATE KEY-----
        PEM skFile = new PEM((java.security.interfaces.RSAPrivateKey) keyPair.getPrivate());
        // -----END RSA PRIVATE KEY-----

        dictionary.put("data", pkFile.toString() + "\n" + skFile.toString());

        // other parameters
        dictionary.put("mode", "ECB");
        dictionary.put("padding", "PKCS1");
        dictionary.put("digest", "SHA256");

        return keyPair;
    }

    private static KeyPair parse(String fileContent)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        PEM pemFile = new PEM(fileContent);
        KeyFactory factory;
        try {
            factory = KeyFactory.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
            factory = KeyFactory.getInstance("RSA");
        }
        // private key
        java.security.interfaces.RSAPrivateKey privateKey;
        byte[] privateKeyData = pemFile.privateKeyData;
        if (privateKeyData != null) {
            // PKCS#8
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyData);
            privateKey = (java.security.interfaces.RSAPrivateKey) factory.generatePrivate(keySpec);
        } else {
            privateKey = null;
        }
        // public key
        java.security.interfaces.RSAPublicKey publicKey;
        byte[] publicKeyData = pemFile.publicKeyData;
        if (publicKeyData != null) {
            // X.509
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyData);
            publicKey = (java.security.interfaces.RSAPublicKey) factory.generatePublic(keySpec);
        } else {
            publicKey = null;
        }
        return new KeyPair(publicKey, privateKey);
    }

    @Override
    public byte[] getData() {
        return privateKey == null ? null : privateKey.getEncoded();
    }

    @Override
    public PublicKey getPublicKey() {
        if (publicKey == null) {
            throw new NullPointerException("public key not found");
        }
        String pem;
        try {
            PEM pemFile = new PEM(publicKey);
            pem = pemFile.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new NullPointerException("generate public key content failed");
        }
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", RSA);
        dictionary.put("data", pem);
        dictionary.put("mode", "ECB");
        dictionary.put("padding", "PKCS1");
        dictionary.put("digest", "SHA256");
        try {
            return new RSAPublicKey(dictionary);
        } catch (NoSuchFieldException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != keySize()) {
            throw new InvalidParameterException("RSA cipher text length error: " + ciphertext.length);
        }
        try {
            Cipher cipher;
            try {
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            } catch (NoSuchAlgorithmException e) {
                //e.printStackTrace();
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            }
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(ciphertext);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] sign(byte[] data) {
        try {
            Signature signer;
            try {
                signer = Signature.getInstance("SHA256withRSA", "BC");
            } catch (NoSuchAlgorithmException e) {
                //e.printStackTrace();
                signer = Signature.getInstance("SHA256withRSA");
            }
            signer.initSign(privateKey);
            signer.update(data);
            return signer.sign();
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
}
