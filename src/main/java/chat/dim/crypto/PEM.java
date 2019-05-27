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

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

public final class PEM {

    private final String fileContent; // PKCS#1 @DER @PEM

    final byte[] publicKeyData;       // X.509
    final byte[] privateKeyData;      // PKCS#8

    PEM(String fileContent) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        super();

        this.fileContent = fileContent;
        this.publicKeyData = getPublicKeyData(fileContent);
        this.privateKeyData = getPrivateKeyData(fileContent);
    }

    PEM(java.security.interfaces.RSAPublicKey publicKey) throws IOException {
        super();

        this.fileContent = getFileContent(publicKey);
        this.publicKeyData = publicKey.getEncoded();
        this.privateKeyData = null;
    }

    PEM(java.security.interfaces.RSAPrivateKey privateKey) throws IOException {
        super();

        this.fileContent = getFileContent(privateKey);
        this.publicKeyData = null;
        this.privateKeyData = privateKey.getEncoded();
    }

    public String toString() {
        return fileContent;
    }

    private static String getFileContent(java.security.interfaces.RSAPublicKey publicKey) throws IOException {
        byte[] data = publicKey.getEncoded();
        String format = publicKey.getFormat();
        if (format.equals("X.509")) {
            // convert to PKCS#1
            data = (new X509(data)).toPKCS1();
        }
        String base64 = Base64.encode(data);
        return "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----";
    }

    private static String getFileContent(java.security.interfaces.RSAPrivateKey privateKey) throws IOException {
        byte[] data = privateKey.getEncoded();
        String format = privateKey.getFormat();
        if (format.equals("PKCS#8")) {
            // convert to PKCS#1
            data = (new PKCS8(data)).toPKCS1();
        }
        String base64 = Base64.encode(data);
        return "-----BEGIN RSA PRIVATE KEY-----\n" + base64 + "\n-----END RSA PRIVATE KEY-----";
    }

    private static byte[] getPublicKeyData(String pem) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        String keyContent = getKeyContent(pem, "PUBLIC");
        boolean isPrivate = false;
        if (keyContent == null) {
            // get from private key content
            keyContent = getKeyContent(pem, "PRIVATE");
            if (keyContent == null) {
                return null;
            }
            isPrivate = true;
        }
        byte[] data = Base64.decode(keyContent);
        String format = "PKCS#1"; // TODO: check key data format
        if (format.equals("PKCS#1")) {
            // convert to X.509
            data = (new PKCS1(data, isPrivate)).toX509();
        }
        return data;
    }

    private static byte[] getPrivateKeyData(String pem) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        String keyContent = getKeyContent(pem, "PRIVATE");
        if (keyContent == null) {
            return null;
        }
        byte[] data = Base64.decode(keyContent);
        String format = "PKCS#1"; // TODO: check key data format
        if (format.equals("PKCS#1")) {
            // convert to PKCS#8
            data = (new PKCS1(data, true)).toPKCS8();
        }
        return data;
    }

    private static String getKeyContent(String pem, String tag) {
        String sTag, eTag;
        int sPos, ePos;

        String content = null;

        sTag = "-----BEGIN RSA " + tag + " KEY-----";
        eTag = "-----END RSA " + tag + " KEY-----";
        sPos = pem.indexOf(sTag);
        if (sPos < 0) {
            sTag = "-----BEGIN " + tag + " KEY-----";
            eTag = "-----END " + tag + " KEY-----";
            sPos = pem.indexOf(sTag);
        }
        if (sPos != -1) {
            sPos += sTag.length();
            ePos = pem.indexOf(eTag, sPos);
            if (ePos != -1) {
                // got it
                content = pem.substring(sPos, ePos);
            }
        }

        if (content == null) {
            // not found
            return null;
        }

        content = content.replaceAll("\r", "");
        content = content.replaceAll("\n", "");
        content = content.replaceAll("\t", "");
        content = content.replaceAll(" ", "");
        return content;
    }
}

/*
 *  X.509 -- https://tools.ietf.org/html/rfc5280
 */
final class X509 {
    private final byte[] data;

    /*
    static byte[] header = { 48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -115, 0 };
    */

    X509(byte[] data) {
        this.data = data;
    }

    // convert X.509 to PKCS#1
    byte[] toPKCS1() throws IOException {
        /*
        int from = header.length;
        int to = data.length;
        int length = to - from;
        if (length <= 0) {
            throw new ArrayIndexOutOfBoundsException("public key data not in X.509 format");
        }
        byte[] pkcs1 = new byte[length];
        System.arraycopy(data, from, pkcs1, 0, length);
        return pkcs1;
        */
        SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(data);
        ASN1Primitive primitive = keyInfo.parsePublicKey();
        return primitive.getEncoded();
    }
}

/*
 *  PKCS#1 -- https://tools.ietf.org/html/rfc3447
 */
final class PKCS1 {
    private final byte[] data;
    private final boolean isPrivate;

    PKCS1(byte[] data, boolean isPrivate) {
        this.data = data;
        this.isPrivate = isPrivate;
    }

    // TODO: convert PKCS#1 to X.509
    byte[] toX509() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        /*
        byte[] header = X509.header;
        byte[] out = new byte[header.length + data.length];
        System.arraycopy(header, 0, out, 0, header.length);
        System.arraycopy(data, 0, out, header.length, data.length);
        return out;
        */
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        if (isPrivate) {
            // get public key data from private key data
            org.bouncycastle.asn1.pkcs.RSAPrivateKey privateKey;
            privateKey = org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(data);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent());
            return keyFactory.generatePublic(keySpec).getEncoded();
        }
        org.bouncycastle.asn1.pkcs.RSAPublicKey publicKey;
        publicKey = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(data);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(publicKey.getModulus(), publicKey.getPublicExponent());
        return keyFactory.generatePublic(keySpec).getEncoded();
    }

    // convert PKCS#1 to PKCS#8
    byte[] toPKCS8() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (!isPrivate) {
            throw new InvalidKeySpecException("it's not private key data");
        }
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        org.bouncycastle.asn1.pkcs.RSAPrivateKey privateKey;
        privateKey = org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(data);
        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(privateKey.getModulus(), privateKey.getPublicExponent(),
                privateKey.getPrivateExponent(), privateKey.getPrime1(), privateKey.getPrime2(),
                privateKey.getExponent1(), privateKey.getExponent2(), privateKey.getCoefficient());
        return keyFactory.generatePrivate(keySpec).getEncoded();
    }
}

/*
 *  PKCS#8 -- https://tools.ietf.org/html/rfc5208
 */
final class PKCS8 {
    private final byte[] data;

    PKCS8(byte[] data) {
        this.data = data;
    }

    // convert PKCS#8 to PKCS#1
    byte[] toPKCS1() throws IOException {
        PrivateKeyInfo keyInfo = PrivateKeyInfo.getInstance(data);
        ASN1Primitive primitive = keyInfo.parsePrivateKey().toASN1Primitive();
        return primitive.getEncoded();
    }
}
