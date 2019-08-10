
import chat.dim.crypto.impl.PrivateKeyImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

import chat.dim.format.*;
import chat.dim.crypto.Digest;
import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.Meta;
import chat.dim.mkm.entity.NetworkType;

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

    @Test
    public void testMeta() throws ClassNotFoundException {
        String username = "moky";
        PrivateKey sk = PrivateKeyImpl.generate("RSA");
        Meta meta = Meta.generate(Meta.VersionDefault, sk, username);
        Log.info("meta: " + JSON.encode(meta));
        Log.info("SK: " + JSON.encode(sk));
    }

    private void checkX(String metaJson, String skJson) throws ClassNotFoundException {
        Object metaDict = JSON.decode(metaJson);
        Meta meta = Meta.getInstance(metaDict);
        ID identifier = meta.generateID(NetworkType.Main);
        Log.info("meta: " + meta);
        Log.info("ID: " + identifier);

        Object skDict = JSON.decode(skJson);
        PrivateKey sk = PrivateKeyImpl.getInstance(skDict);
        Log.info("private key: " + sk);
        Assert.assertTrue(meta.key.matches(sk));

        String name = "moky";
        byte[] data = name.getBytes(Charset.forName("UTF-8"));
        byte[] CT = meta.key.encrypt(data);
        byte[] PT = sk.decrypt(CT);
        String hex = Utils.hexEncode(CT);
        String res = new String(PT, Charset.forName("UTF-8"));
        Log.info("encryption: " + name + ", -> " + hex + " -> " + res);
    }

    @Test
    public void testPythonMeta() throws ClassNotFoundException {
        Log.info("checking data from Python ...");
        String s1 = "{\"version\": 1, \"seed\": \"moky\", \"key\": {\"algorithm\": \"RSA\", \"data\": \"-----BEGIN PUBLIC KEY-----\\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDbTf58ScygpB7tbOSN5/9dZIEB\\ncbKjnwUxW8cWdEo765gGXUsNUQUgThFC4csLsFTvqVhGxn3+WfDIwrvzF/2HWJu8\\nUvo3LkG3ZGP8US9y2Mvp9VaCWz8ZwyMe4fcWus8dRs8fdTatLeoZVGQDv9SdE2Vx\\nuPvJw3gHyR9dOKq37QIDAQAB\\n-----END PUBLIC KEY-----\"}, \"fingerprint\": \"MTHQiV/6sCAtOM6CJ2clJgKHu4Lyw34rzoWLsARPL61Z4ivz/q2y9dXIN49A0B+RT6z7+vqI6NGseTLZoc1wT7EHN5qDIYWMdJjOd7YGv5K/AFQCMkZxcpDb51ryWC22/n2bi1MvkH+b3lhQjwvtwqq05K1nDixUTFqAEcNQDOg=\"}\n";
        String s2 = "{\"algorithm\": \"RSA\", \"data\": \"-----BEGIN RSA PRIVATE KEY-----\\nMIICXAIBAAKBgQDbTf58ScygpB7tbOSN5/9dZIEBcbKjnwUxW8cWdEo765gGXUsN\\nUQUgThFC4csLsFTvqVhGxn3+WfDIwrvzF/2HWJu8Uvo3LkG3ZGP8US9y2Mvp9VaC\\nWz8ZwyMe4fcWus8dRs8fdTatLeoZVGQDv9SdE2VxuPvJw3gHyR9dOKq37QIDAQAB\\nAoGADLCdbHM3xkbg7EOsEQMO7YhKh7scx2eE/SdupH+7qO53yEx/MojQ517lFE3s\\n+iLss0aFD2lecoihTHiqOAWYG7CfMRg7OaG5Kx7MCZdo/fm28DtiymjpB6nR1SFu\\nmroBWN2rDWHiYBSLeZM8Efh8ONhSue6zMwCIzatBfhrp5JkCQQDfuevIWO9WYRQi\\ndpvDdHzf7uK7ypAs+h90NdJ5P2ihYW7EkSioTu1tskI+d71p/pzTWMd6u/3wWKGV\\n95Yz2wQ5AkEA+vDJToLYObUStmGp+FVsKU4JezHfd831pMcx09nG3d3AIHcJfKx7\\nJzY6k1KVUE3nPHMwhIxEV1AOsOiImU7ZVQJBAKL7b5AZceoMeL2OiHTAJMSB470I\\nmTWa1VU0bGsVzWRbdXVPhj3umbrjNK0LT/qqmJbCwzdfQmRYPQbiQhLux8kCQG4x\\nzISwipkUvcnfK0+E24Fr5lf196bZh7Q7UNMx/9Uv6o2XGFBqQY5fjutgyXbBLvjp\\nsHWUTvJ0km73Pfzslh0CQGLWIE3osF9bLOy7xhLzbVFf3y0yFI/0/pyYvBMZ3yCy\\nw65R9OCcY2PFM2SGEw+nQtopShcpKT0xG30P11TO9oA=\\n-----END RSA PRIVATE KEY-----\"}\n";
        checkX(s1, s2);
    }

    @Test
    public void testOCMeta() throws ClassNotFoundException {
        Log.info("checking data from Obj-C ...");
        String s1 = "{\"fingerprint\":\"HLpTHlWIr\\/Hzd7l9\\/+8iAYgeOQI2gezR4TLs\\/WYR0sJZYKnVJeHeTijbwtze0t7Ak\\/K7U8TVnnlubRXE7N\\/Dio0\\/1PZmhoW95j40zVOjlxc13n54S4ZKVH7laZUmGtNLEkKsj+4Vr\\/RybJJhGQMwHO2h3q0ueIQj6nrI6fWCX9I=\",\"key\":{\"algorithm\":\"RSA\",\"data\":\"-----BEGIN PUBLIC KEY-----\\nMIGJAoGBANHKUezJ7gH1RTnYhnRZrMrONd3\\/RKV+UthgU4uwVsA7jJz\\/JwJWhsLY\\nX1BsSJRcQRnpWYSUxkzjU34FhUW28oFPvTrtzFM41AhLhE8MYP8vy9\\/\\/TK0OAFwY\\nwY1pqEJJKdbbOVCHLI1Ddu1CbHmLdQ2NF+10nszrJGxJLDnNadZnAgMBAAE=\\n-----END PUBLIC KEY-----\\n\",\"digest\":\"SHA256\",\"mode\":\"ECB\",\"padding\":\"PKCS1\"},\"seed\":\"moky\",\"version\":1}";
        String s2 = "{\"algorithm\":\"RSA\",\"data\":\"-----BEGIN RSA PRIVATE KEY-----\\nMIICXAIBAAKBgQDRylHsye4B9UU52IZ0WazKzjXd\\/0SlflLYYFOLsFbAO4yc\\/ycC\\nVobC2F9QbEiUXEEZ6VmElMZM41N+BYVFtvKBT7067cxTONQIS4RPDGD\\/L8vf\\/0yt\\nDgBcGMGNaahCSSnW2zlQhyyNQ3btQmx5i3UNjRftdJ7M6yRsSSw5zWnWZwIDAQAB\\nAoGAKeT00kwK9yIjVmdqhk6oJoHimPgSndfptGMcG\\/+1e0MJFAsSH7HmzH9IHXfa\\nUKJRr9p9MXBCX3VgJYD1udPMfnCxCnL9CLnqxjPWJ+SISumV2g8PYVEPCVnN+zBp\\njBLpoeQS43c4heyF3DM41x6QrSGXtofUJ1W4U0VejnvlosECQQDvqp\\/6rkT4mjqj\\nMAGHWnIr2cbnt2UqQH85viSx3pLyPXn5FnDI1EiEU\\/Pi+XuxoTCtxWd+gx9aWRpY\\n+mXcK\\/YnAkEA4BZ0ukPDr8e5KmQN7x5x\\/CfHhqPRGVk4VH9z+icJ0\\/DvH9+7Nj30\\n5i8T6kAyGWdYoxkhQydmwi6Fpx6SxGWFwQJAQxkV6OzZSnCDciSCiQ59YGF8Gmtx\\n2z5rYBMn2tRhd4hWmbH6qX8lPkbyxNzsEHL8Weoma3jyUi0X\\/0k7M0TriQJBALv6\\nGnEl50HNiMbGp+mu4G9l7zpCsWVSMq6vO9rcZKIlunJCfAlEb+uoEkyvDVfCGdi3\\ne++ZXdoGrJdETlnx0AECQDQEW7kuBhHQ4cZ9v+qY7PfM87qM7EdsCm1QTNJ48n9I\\nJWeoVaoTQkKb+vB\\/JOjuNx9FHmqySKkNMkTqPNlFCFI=\\n-----END RSA PRIVATE KEY-----\\n\",\"digest\":\"SHA256\",\"mode\":\"ECB\",\"padding\":\"PKCS1\"}";
        checkX(s1, s2);
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
