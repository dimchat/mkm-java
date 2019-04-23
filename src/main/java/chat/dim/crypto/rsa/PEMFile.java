package chat.dim.crypto.rsa;

import chat.dim.crypto.Utils;

class PEMFile {

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
            this.publicKeyData = Utils.base64Decode(publicContent);
        }
        // private key
        String privateContent = getKeyContent(fileContent, "PRIVATE");
        if (privateContent == null) {
            this.privateKeyData = null;
        } else {
            this.privateKeyData = Utils.base64Decode(privateContent);
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
        String base64 = Utils.base64Encode(data);
        return "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----";
    }

    private String getFileContent(java.security.interfaces.RSAPrivateKey privateKey) {
        byte[] data = privateKey.getEncoded();
        String base64 = Utils.base64Encode(data);
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
