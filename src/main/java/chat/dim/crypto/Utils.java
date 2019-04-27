package chat.dim.crypto;

import chat.dim.crypto.bitcoinj.Base58;
import chat.dim.crypto.bouncycastle.RIPEMD160Digest;

import com.alibaba.fastjson.JSON;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

public class Utils {

    public static String jsonEncode(Object container) {
        return JSON.toJSONString(container);
    }

    public static Map<String, Object> jsonDecode(String jsonString) {
        return JSON.parseObject(jsonString);
    }

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
}
