import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
import org.junit.Test;

import java.util.Map;

public class CryptoECCTest {

    @Test
    public void testECC() {
    }
}

final class ECCPrivateKey extends PrivateKey {

    public ECCPrivateKey(Map<String, Object> dictionary) {
        super(dictionary);
    }

    @Override
    public PublicKey getPublicKey() {
        return null;
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) {
        return new byte[0];
    }

    @Override
    public byte[] sign(byte[] data) {
        return new byte[0];
    }

    static {
        PrivateKey.register(PrivateKey.ECC, ECCPrivateKey.class);
    }
}

final class ECCPublicKey extends PublicKey {

    public ECCPublicKey(Map<String, Object> dictionary) {
        super(dictionary);
    }

    @Override
    public byte[] encrypt(byte[] plaintext) {
        return new byte[0];
    }

    @Override
    public boolean verify(byte[] data, byte[] signature) {
        return false;
    }

    static {
        PublicKey.register(PublicKey.ECC, ECCPublicKey.class);
    }
}
