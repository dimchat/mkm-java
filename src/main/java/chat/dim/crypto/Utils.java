package chat.dim.crypto;

import chat.dim.crypto.bitcoinj.Base58;
import chat.dim.crypto.bouncycastle.RIPEMD160Digest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utils {

    public static String base64Encode(byte[] data) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(data);
    }

    public static byte[] base64Decode(String string) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(string);
    }

    public static String base58Encode(byte[] data) {
        return Base58.encode(data);
    }

    public static byte[] base58Decode(String string) {
        return Base58.decode(string);
    }

    public static byte[] sha256(byte[] data) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        md.reset();
        md.update(data);
        return md.digest();
    }

    public static byte[] ripemd160(byte[] data) {
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(data, 0, data.length);
        byte[] out = new byte[20];
        digest.doFinal(out, 0);
        return out;
    }

    public static String hexEncode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String hex;
        for (byte ch : data) {
            hex = Integer.toHexString(ch & 0xFF);
            sb.append(hex.length() == 1 ? "0" + hex : hex);
        }
        return sb.toString();
    }

    public static void main(String args[]) throws UnsupportedEncodingException {
        System.out.println("Crypto test");

        String string = "moky";
        byte[] data = string.getBytes("UTF-8");

        byte[] hash;
        String res;
        String exp;

        // sha256（moky）= cb98b739dd699aa44bb6ebba128d20f2d1e10bb3b4aa5ff4e79295b47e9ed76d
        hash = Utils.sha256(data);
        res = Utils.hexEncode(hash);
        exp = "cb98b739dd699aa44bb6ebba128d20f2d1e10bb3b4aa5ff4e79295b47e9ed76d";
        System.out.println("sha256(" + string + ") = " + res);
        if (!res.equalsIgnoreCase(exp)) {
            throw new ArithmeticException("SHA256 error");
        }

        // ripemd160(moky) = 44bd174123aee452c6ec23a6ab7153fa30fa3b91
        hash = Utils.ripemd160(data);
        res = Utils.hexEncode(hash);
        exp = "44bd174123aee452c6ec23a6ab7153fa30fa3b91";
        System.out.println("ripemd160(" + string + ") = " + res);
        if (!res.equalsIgnoreCase(exp)) {
            throw new ArithmeticException("RIPEMD160 error");
        }

        // base58(moky) = 3oF5MJ
        res = Utils.base58Encode(data);
        exp = "3oF5MJ";
        System.out.println("base58(" + string + ") = " + res);
        if (!res.equalsIgnoreCase(exp)) {
            throw new ArithmeticException("BASE58 error");
        }

        // base64(moky) = bW9reQ==
        res = Utils.base64Encode(data);
        exp = "bW9reQ==";
        System.out.println("base64(" + string + ") = " + res);
        if (!res.equalsIgnoreCase(exp)) {
            throw new ArithmeticException("BASE64 error");
        }
    }
}
