package chat.dim.crypto;

import java.util.Map;

public abstract class CryptographyKey {

    protected final Map<String, Object> dictionary;

    public final String algorithm;
    public byte[] data;

    public CryptographyKey(CryptographyKey key) {
        super();
        this.dictionary = key.dictionary;
        this.algorithm = key.algorithm;
        this.data = key.data;
    }

    public CryptographyKey(Map<String, Object> dictionary) {
        super();
        this.dictionary = dictionary;
        this.algorithm = CryptographyKey.getAlgorithm(dictionary);
        // process by subclass
        this.data = null;
    }

    public String toString() {
        return dictionary.toString();
    }

    public Map<String, Object> toDictionary() {
        return dictionary;
    }

    public boolean equals(CryptographyKey key) {
        return key != null && equals(key.dictionary);
    }

    public boolean equals(Map map) {
        return dictionary.equals(map);
    }

    protected static String getAlgorithm(Map<String, Object> dictionary) {
        return (String) dictionary.get("algorithm");
    }
}
