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
package chat.dim.format;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import chat.dim.plugins.PEMFile;

public class PEM {

    public static String encodePublicKey(PublicKey key) {
        return parser.encodePublicKey(key);
    }

    public static String encodePrivateKey(PrivateKey key) {
        return parser.encodePrivateKey(key);
    }

    public static PublicKey decodePublicKey(String pem) {
        return parser.decodePublicKey(pem);
    }

    public static PrivateKey decodePrivateKey(String pem) {
        return parser.decodePrivateKey(pem);
    }

    // default parser
    public static KeyParser parser = new KeyParser() {

        @Override
        public String encodePublicKey(PublicKey key) {
            try {
                PEMFile file = new PEMFile(key);
                return file.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public String encodePrivateKey(PrivateKey key) {
            try {
                PEMFile file = new PEMFile(key);
                return file.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public PublicKey decodePublicKey(String pem) {
            PEMFile file = null;
            try {
                file = new PEMFile(pem);
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
        public PrivateKey decodePrivateKey(String pem) {
            PEMFile file = null;
            try {
                file = new PEMFile(pem);
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
