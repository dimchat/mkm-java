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

final class PEMFile {

    private final String fileContent;

    final byte[] publicKeyData;
    final byte[] privateKeyData;

    PEMFile(String fileContent) {
        super();

        this.fileContent = fileContent;
        // public key
        String publicContent = getKeyContent(fileContent, "PUBLIC");
        if (publicContent == null) {
            this.publicKeyData = null;
        } else {
            this.publicKeyData = Base64.decode(publicContent);
        }
        // private key
        String privateContent = getKeyContent(fileContent, "PRIVATE");
        if (privateContent == null) {
            this.privateKeyData = null;
        } else {
            this.privateKeyData = Base64.decode(privateContent);
        }
    }

    PEMFile(java.security.interfaces.RSAPublicKey publicKey) {
        super();

        this.fileContent = getFileContent(publicKey);
        this.publicKeyData = publicKey.getEncoded();
        this.privateKeyData = null;
    }

    PEMFile(java.security.interfaces.RSAPrivateKey privateKey) {
        super();

        this.fileContent = getFileContent(privateKey);
        this.publicKeyData = null;
        this.privateKeyData = privateKey.getEncoded();
    }

    public String toString() {
        return fileContent;
    }

    private String getFileContent(java.security.interfaces.RSAPublicKey publicKey) {
        byte[] data = publicKey.getEncoded();
        String base64 = Base64.encode(data);
        return "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----";
    }

    private String getFileContent(java.security.interfaces.RSAPrivateKey privateKey) {
        byte[] data = privateKey.getEncoded();
        String base64 = Base64.encode(data);
        return "-----BEGIN RSA PRIVATE KEY-----\n" + base64 + "\n-----END RSA PRIVATE KEY-----";
    }

    private String getKeyContent(String pem, String tag) {
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
