import chat.dim.crypto.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CryptoTest {

    private void log(String msg) {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String method = traces[2].getMethodName();
        int line = traces[2].getLineNumber();
        System.out.println("[" + method + ":" + line + "] " + msg);
    }

    private static String hexEncode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String hex;
        for (byte ch : data) {
            hex = Integer.toHexString(ch & 0xFF);
            sb.append(hex.length() == 1 ? "0" + hex : hex);
        }
        return sb.toString();
    }

    @Test
    public void testHash() throws UnsupportedEncodingException {
        log("Crypto test");

        String string = "moky";
        byte[] data = string.getBytes("UTF-8");

        byte[] hash;
        String res;
        String exp;

        // sha256（moky）= cb98b739dd699aa44bb6ebba128d20f2d1e10bb3b4aa5ff4e79295b47e9ed76d
        hash = Digest.sha256(data);
        assert hash != null;
        res = hexEncode(hash);
        exp = "cb98b739dd699aa44bb6ebba128d20f2d1e10bb3b4aa5ff4e79295b47e9ed76d";
        log("sha256(" + string + ") = " + res);
        Assert.assertEquals(exp, res);

        // ripemd160(moky) = 44bd174123aee452c6ec23a6ab7153fa30fa3b91
        hash = Digest.ripemd160(data);
        res = hexEncode(hash);
        exp = "44bd174123aee452c6ec23a6ab7153fa30fa3b91";
        log("ripemd160(" + string + ") = " + res);
        Assert.assertEquals(exp, res);
    }

    @Test
    public void testEncode() throws UnsupportedEncodingException {
        String string = "moky";
        byte[] data = string.getBytes("UTF-8");

        String res;
        String exp;

        // base58(moky) = 3oF5MJ
        res = Base58.encode(data);
        exp = "3oF5MJ";
        log("base58(" + string + ") = " + res);
        Assert.assertEquals(exp, res);

        // base64(moky) = bW9reQ==
        res =Base64.encode(data);
        exp = "bW9reQ==";
        log("base64(" + string + ") = " + res);
        Assert.assertEquals(exp, res);
    }

    @Test
    public void testRSA() throws ClassNotFoundException, UnsupportedEncodingException {
        PrivateKey sk = PrivateKey.create(PrivateKey.RSA);
        log("RSA private key:" + sk);

        PublicKey pk = sk.getPublicKey();
        log("RSA public key:" + pk);

        String text = "moky";
        byte[] plaintext = text.getBytes("UTF-8");
        byte[] cipherText = pk.encrypt(plaintext);
        log("RSA encrypt(\"" + text + "\") = " + hexEncode(cipherText));

        byte[] data = sk.decrypt(cipherText);
        String decrypt = new String(data);
        log("decrypt to " + decrypt);

        Assert.assertEquals(text, decrypt);

        byte[] signature = sk.sign(plaintext);
        log("signature(\"" + text + "\") = " + hexEncode(signature));

        Assert.assertTrue(pk.verify(plaintext, signature));
    }

    @Test
    public void testPublicKey() throws ClassNotFoundException {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", "RSA");
        dictionary.put("data", "-----BEGIN PUBLIC KEY-----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCI8jKup683D4Gxa0dJomGMqHhd3bcHr7NObJuglNLvYir9PFsfs/mNB/K6jN+R+O6hpyCIiKARk0zxxfuzzLdZhXWmqcvy4f95cJAG5aYOtv8RACwRo/b9/NaDuHnpBW7soArZDS8RqTI1lYH5v2tZqMIdhoC5DAUyKOHFcGxiGQIDAQAB\n" +
                "-----END PUBLIC KEY-----");

        PublicKey key = PublicKey.getInstance(dictionary);
        log("key:" + key);
    }

    @Test
    public void testAES() throws ClassNotFoundException, UnsupportedEncodingException {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", "AES");
        dictionary.put("data", "C2+xGizLL1G1+z9QLPYNdp/bPP/seDvNw45SXPAvQqk=");
        dictionary.put("iv", "SxPwi6u4+ZLXLdAFJezvSQ==");

        SymmetricKey key = SymmetricKey.getInstance(dictionary);
        log("key:" + key);

        String text = "moky";
        byte[] plaintext = text.getBytes("UTF-8");
        byte[] cipherText = key.encrypt(plaintext);
        log("encrypt(\"" + text + "\") = " + hexEncode(cipherText));

        byte[] data = key.decrypt(cipherText);
        String decrypt = new String(data, "UTF-8");
        log("decrypt to " + decrypt);
        log(text + " -> " + Base64.encode(cipherText) + " -> " + decrypt);

        Assert.assertEquals("0xtbqZN6x2aWTZn0DpCoCA==", Base64.encode(cipherText));

        SymmetricKey key2 = SymmetricKey.getInstance(dictionary);
        log("key2: " + key2);
        Assert.assertEquals(key, key2);
//        Assert.assertTrue(key.equals(key2));

        text = "XX5qfromb3R078VVK7LwVA==";
//        text = "0xtbqZN6x2aWTZn0DpCoCA==";
        cipherText = Base64.decode(text);
        plaintext = key2.decrypt(cipherText);
        log("FIXED: " + text + " -> " + (plaintext == null ? null : new String(plaintext)));
//        plaintext = key2.decrypt(cipherText);
//        log("FIXED: " + text + " -> " + (plaintext == null ? null : new String(plaintext)));

        // random key
        key = SymmetricKey.create(SymmetricKey.AES);
        log("key: " + key);

        text = "moky";
        plaintext = text.getBytes("UTF-8");
        cipherText = key.encrypt(plaintext);
        log("encrypt(\"" + text + "\") = " + hexEncode(cipherText));

        data = key.decrypt(cipherText);
        decrypt = new String(data, "UTF-8");
        log("decrypt to " + decrypt);

        Assert.assertEquals(text, decrypt);
    }
}
