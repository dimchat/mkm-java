import chat.dim.crypto.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CryptoRSATest {

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
                "MIGJAoGBALB+vbUK48UU9rjlgnohQowME+3JtTb2hLPqtatVOW364/EKFq0/PSdnZVE9V2Zq+pbX7dj3nCS4pWnYf40ELH8wuDm0Tc4jQ70v4LgAcdy3JGTnWUGiCsY+0Z8kNzRkm3FJid592FL7ryzfvIzB9bjg8U2JqlyCVAyUYEnKv4lDAgMBAAE=\n" +
                "-----END PUBLIC KEY-----");

        PublicKey key = PublicKey.getInstance(dictionary);
        log("public key:" + key);
    }

    @Test
    public void testPrivateKey() throws ClassNotFoundException {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("algorithm", "RSA");
        dictionary.put("data", "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIICXQIBAAKBgQCwfr21CuPFFPa45YJ6IUKMDBPtybU29oSz6rWrVTlt+uPxChatPz0nZ2VRPVdmavqW1+3Y95wkuKVp2H+NBCx/MLg5tE3OI0O9L+C4AHHctyRk51lBogrGPtGfJDc0ZJtxSYnefdhS+68s37yMwfW44PFNiapcglQMlGBJyr+JQwIDAQABAoGAVc0HhJ/KouDSIIjSqXTJ2TN17L+GbTXixWRw9N31kVXKwj9ZTtfTbviA9MGRX6TaNcK7SiL1sZRiNdaeC3vf9RaUe3lV3aR/YhxuZ5bTQNHPYqJnbbwsQkp4IOwSWqOMCfsQtP8O+2DPjC8Jx7PPtOYZ0sC5esMyDUj/EDv+HUECQQDXsPlTb8BAlwWhmiAUF8ieVENR0+0EWWU5HV+dp6Mz5gf47hCO9yzZ76GyBM71IEQFdtyZRiXlV9CBOLvdlbqLAkEA0XqONVaW+nNTNtlhJVB4qAeqpj/foJoGbZhjGorBpJ5KPfpD5BzQgsoT6ocv4vOIzVjAPdk1lE0ACzaFpEgbKQJBAKDLjUO3ZrKAI7GSreFszaHDHaCuBd8dKcoHbNWiOJejIERibbO27xfVfkyxKvwwvqT4NIKLegrciVMcUWliivsCQQCiA1Z/XEQS2iUO89tVn8JhuuQ6Boav0NCN7OEhQxX3etFS0/+0KrD9psr2ha38qnwwzaaJbzgoRdF12qpL39TZAkBPv2lXFNsn0/Jq3cUemof+5sm53KvtuLqxmZfZMAuTSIbB+8i05JUVIc+mcYqTqGp4FDfz6snzt7sMBQdx6BZY\n" +
                "-----END RSA PRIVATE KEY-----");

        PrivateKey sk = PrivateKey.getInstance(dictionary);
        log("private key:" + sk);
    }
}