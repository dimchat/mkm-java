package chat.dim.crypto;

import java.util.HashMap;

public abstract class CryptographyKey {

    protected final HashMap<String, Object> dictionary;

    public final String algorithm;
    public byte[] data;

    public CryptographyKey(CryptographyKey key) {
        super();
        this.dictionary = key.dictionary;
        this.algorithm = key.algorithm;
        this.data = key.data;
    }

    public CryptographyKey(HashMap<String, Object> dictionary) {
        super();
        this.dictionary = dictionary;
        this.algorithm = CryptographyKey.getAlgorithm(dictionary);
        // process by subclass
        this.data = null;
    }

    public String toString() {
        return dictionary.toString();
    }

    public HashMap<String, Object> toDictionary() {
        return dictionary;
    }

    public static String getAlgorithm(HashMap<String, Object> dictionary) {
        return (String) dictionary.get("algorithm");
    }

    public boolean equals(CryptographyKey key) {
        return equals(key.dictionary);
    }

    public boolean equals(HashMap map) {
        return dictionary.equals(map);
    }
}
