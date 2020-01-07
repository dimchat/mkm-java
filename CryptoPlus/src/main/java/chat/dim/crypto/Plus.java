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

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import chat.dim.digest.Hash;
import chat.dim.digest.RIPEMD160;
import chat.dim.format.*;
import chat.dim.plugins.AESKey;
import chat.dim.plugins.PEMContent;
import chat.dim.plugins.RSAPrivateKey;
import chat.dim.plugins.RSAPublicKey;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

public class Plus {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        /*
         *  Symmetric Key
         */
        // AES
        SymmetricKey.register(SymmetricKey.AES, AESKey.class); // default
        SymmetricKey.register("AES/CBC/PKCS7Padding", AESKey.class);

        // DES
        // ...

        /*
         *  Private Key
         */
        // RSA
        PrivateKey.register(PrivateKey.RSA, RSAPrivateKey.class); // default
        PrivateKey.register("SHA256withRSA", RSAPrivateKey.class);
        PrivateKey.register("RSA/ECB/PKCS1Padding", RSAPrivateKey.class);

        // ECC
        // ...

        /*
         *  Public Key
         */
        // RSA
        PublicKey.register(PublicKey.RSA, RSAPublicKey.class); // default
        PublicKey.register("SHA256withRSA", RSAPublicKey.class);
        PublicKey.register("RSA/ECB/PKCS1Padding", RSAPublicKey.class);

        // ECC
        // ...

        /*
         *  Digest
         */
        // RIPEMD160
        RIPEMD160.hash = new Hash() {
            @Override
            public byte[] digest(byte[] data) {
                RIPEMD160Digest digest = new RIPEMD160Digest();
                digest.update(data, 0, data.length);
                byte[] out = new byte[20];
                digest.doFinal(out, 0);
                return out;
            }
        };

        /*
         *  Format
         */
        // HEX coding
        Hex.coder = new BaseCoder() {
            @Override
            public String encode(byte[] data) {
                return org.bouncycastle.util.encoders.Hex.toHexString(data);
            }

            @Override
            public byte[] decode(String string) {
                return org.bouncycastle.util.encoders.Hex.decode(string);
            }
        };

        // JsON
        JSON.parser = new DataParser() {
            @Override
            public String encode(Object container) {
                return com.alibaba.fastjson.JSON.toJSONString(container);
            }

            @Override
            public Object decode(String json) {
                return com.alibaba.fastjson.JSON.parse(json);
            }
        };

        // PEM
        PEM.parser = new KeyParser() {
            @Override
            public String encodePublicKey(java.security.PublicKey publicKey) {
                try {
                    return (new PEMContent(publicKey)).toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public String encodePrivateKey(java.security.PrivateKey privateKey) {
                try {
                    return (new PEMContent(privateKey)).toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public java.security.PublicKey decodePublicKey(String pem) {
                PEMContent file = null;
                try {
                    file = new PEMContent(pem);
                } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
                    e.printStackTrace();
                }
                byte[] keyData = file == null ? null : file.publicKeyData;
                if (keyData != null) {
                    // X.509
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyData);
                    try {
                        return getFactory().generatePublic(keySpec);
                    } catch (InvalidKeySpecException | NoSuchProviderException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            public java.security.PrivateKey decodePrivateKey(String pem) {
                PEMContent file = null;
                try {
                    file = new PEMContent(pem);
                } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
                    e.printStackTrace();
                }
                byte[] keyData = file == null ? null : file.privateKeyData;
                if (keyData != null) {
                    // PKCS#8
                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyData);
                    try {
                        return getFactory().generatePrivate(keySpec);
                    } catch (InvalidKeySpecException | NoSuchProviderException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            private KeyFactory getFactory() throws NoSuchProviderException, NoSuchAlgorithmException {
                try {
                    return KeyFactory.getInstance("RSA", "BC");
                } catch (NoSuchAlgorithmException e) {
                    //e.printStackTrace();
                    return KeyFactory.getInstance("RSA");
                }
            }
        };
    }
}
