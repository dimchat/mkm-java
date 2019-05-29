import chat.dim.crypto.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CryptoTest {

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
        Log.info("Crypto test");

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
        Log.info("sha256(" + string + ") = " + res);
        Assert.assertEquals(exp, res);

        // ripemd160(moky) = 44bd174123aee452c6ec23a6ab7153fa30fa3b91
        hash = Digest.ripemd160(data);
        res = hexEncode(hash);
        exp = "44bd174123aee452c6ec23a6ab7153fa30fa3b91";
        Log.info("ripemd160(" + string + ") = " + res);
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
        Log.info("base58(" + string + ") = " + res);
        Assert.assertEquals(exp, res);

        // base64(moky) = bW9reQ==
        res =Base64.encode(data);
        exp = "bW9reQ==";
        Log.info("base64(" + string + ") = " + res);
        Assert.assertEquals(exp, res);
    }

    @Test
    public void testAES() throws ClassNotFoundException, UnsupportedEncodingException {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", "AES");
        dictionary.put("data", "C2+xGizLL1G1+z9QLPYNdp/bPP/seDvNw45SXPAvQqk=");
        dictionary.put("iv", "SxPwi6u4+ZLXLdAFJezvSQ==");

        SymmetricKey key = SymmetricKey.getInstance(dictionary);
        Log.info("key:" + key);

        String text = "moky";
        byte[] plaintext = text.getBytes("UTF-8");
        byte[] ciphertext = key.encrypt(plaintext);
        Log.info("encrypt(\"" + text + "\") = " + hexEncode(ciphertext));

        byte[] data = key.decrypt(ciphertext);
        String decrypt = new String(data, "UTF-8");
        Log.info("decrypt to " + decrypt);
        Log.info(text + " -> " + Base64.encode(ciphertext) + " -> " + decrypt);

        Assert.assertEquals("0xtbqZN6x2aWTZn0DpCoCA==", Base64.encode(ciphertext));

        SymmetricKey key2 = SymmetricKey.getInstance(dictionary);
        Log.info("key2: " + key2);
        Assert.assertEquals(key, key2);
//        Assert.assertTrue(key.equals(key2));

        text = "XX5qfromb3R078VVK7LwVA==";
//        text = "0xtbqZN6x2aWTZn0DpCoCA==";
        ciphertext = Base64.decode(text);
        plaintext = key2.decrypt(ciphertext);
        Log.info("FIXED: " + text + " -> " + (plaintext == null ? null : new String(plaintext)));
//        plaintext = key2.decrypt(ciphertext);
//        log("FIXED: " + text + " -> " + (plaintext == null ? null : new String(plaintext)));

        // random key
        key = SymmetricKey.create(SymmetricKey.AES);
        Log.info("key: " + key);

        text = "moky";
        plaintext = text.getBytes("UTF-8");
        ciphertext = key.encrypt(plaintext);
        Log.info("encrypt(\"" + text + "\") = " + hexEncode(ciphertext));

        data = key.decrypt(ciphertext);
        decrypt = new String(data, "UTF-8");
        Log.info("decrypt to " + decrypt);

        Assert.assertEquals(text, decrypt);
    }
}
