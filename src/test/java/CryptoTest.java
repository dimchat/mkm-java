import chat.dim.crypto.Digest;
import chat.dim.format.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class CryptoTest {

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
        res = Utils.hexEncode(hash);
        exp = "cb98b739dd699aa44bb6ebba128d20f2d1e10bb3b4aa5ff4e79295b47e9ed76d";
        Log.info("sha256(" + string + ") = " + res);
        Assert.assertEquals(exp, res);

        // ripemd160(moky) = 44bd174123aee452c6ec23a6ab7153fa30fa3b91
        hash = Digest.ripemd160(data);
        res = Utils.hexEncode(hash);
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

    static {
        Base58.coder = new BaseCoder() {

            @Override
            public String encode(byte[] data) {
                return chat.dim.format.bitcoinj.Base58.encode(data);
            }

            @Override
            public byte[] decode(String string) {
                return chat.dim.format.bitcoinj.Base58.decode(string);
            }
        };

        Base64.coder = new BaseCoder() {

            @Override
            public String encode(byte[] data) {
                return java.util.Base64.getEncoder().encodeToString(data);
            }

            @Override
            public byte[] decode(String string) {
                return java.util.Base64.getDecoder().decode(string);
            }
        };

        JSON.parser = new DataParser() {

            @Override
            public String encode(Object container) {
                return com.alibaba.fastjson.JSON.toJSONString(container);
            }

            @Override
            public Object decode(String jsonString) {
                return com.alibaba.fastjson.JSON.parseObject(jsonString);
            }
        };
    }
}
