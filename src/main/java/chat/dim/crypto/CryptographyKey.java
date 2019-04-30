package chat.dim.crypto;

import java.util.Map;

/**
 *  Cryptography Key
 *
 *      keyInfo format: {
 *          algorithm: "RSA", // ECC, AES, ...
 *          data     : "{BASE64_ENCODE}",
 *          ...
 *      }
 */
public abstract class CryptographyKey extends Dictionary {

    protected final String algorithm;
    public byte[] data;

    protected CryptographyKey(Map<String, Object> dictionary) {
        super(dictionary);
        this.algorithm = getAlgorithm(dictionary);
        // process by subclass
        this.data = null;
    }

    protected static String getAlgorithm(Map<String, Object> dictionary) {
        return (String) dictionary.get("algorithm");
    }
}
