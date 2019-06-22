import chat.dim.crypto.SymmetricKey;

import java.util.Map;

public class CryptoDESTest {
}

final class DESKey extends SymmetricKey {

    public DESKey(Map<String, Object> dictionary) {
        super(dictionary);
        // TODO: check algorithm parameters
    }

    @Override
    public byte[] encrypt(byte[] plaintext) {
        return new byte[0];
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) {
        return new byte[0];
    }

    static {
        SymmetricKey.register(SymmetricKey.DES, DESKey.class);
    }
}
