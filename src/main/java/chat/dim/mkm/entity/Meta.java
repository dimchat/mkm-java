package chat.dim.mkm.entity;

import chat.dim.crypto.Dictionary;
import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
import chat.dim.crypto.Utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

/**
 *  Account/Group Meta data
 *
 *      data format: {
 *          version: 1,          // algorithm version
 *          seed: "moKy",        // user/group name
 *          key: "{public key}", // PK = secp256k1(SK);
 *          fingerprint: "..."   // CT = sign(seed, SK);
 *      }
 *
 *      algorithm:
 *          fingerprint = sign(seed, SK);
 *
 *          CT      = fingerprint; // or key.data for BTC address
 *          hash    = ripemd160(sha256(CT));
 *          code    = sha256(sha256(network + hash)).prefix(4);
 *          address = base58_encode(network + hash + code);
 *          number  = uint(code);
 */
public class Meta extends Dictionary {

    public final byte version;
    public final PublicKey key;
    public final String seed;
    public final byte[] fingerprint;
    public final boolean valid;

    /**
     *  @enum MKMMetaVersion
     *
     *  @abstract Defined for algorithm that generating address.
     *
     *  @discussion Generate & check ID/Address
     *
     *      MKMMetaVersion_MKM give a seed string first, and sign this seed to get
     *      fingerprint; after that, use the fingerprint to generate address.
     *      This will get a firmly relationship between (username, address & key).
     *
     *      MKMMetaVersion_BTC use the key data to generate address directly.
     *      This can build a BTC address for the entity ID (no username).
     *
     *      MKMMetaVersion_ExBTC use the key data to generate address directly, and
     *      sign the seed to get fingerprint (just for binding username & key).
     *      This can build a BTC address, and bind a username to the entity ID.
     */
    public static final byte VersionMKM     = 0x01;
    public static final byte VersionBTC     = 0x02;
    public static final byte VersionExBTC   = 0x03;
    public static final byte VersionDefault = VersionMKM;

    public Meta(Map<String, Object> dictionary) throws UnsupportedEncodingException, ClassNotFoundException {
        super(dictionary);
        this.version     = Integer.getInteger((String)dictionary.get("version")).byteValue();
        this.key         = PublicKey.getInstance(dictionary.get("key"));
        this.seed        = (String)dictionary.get("seed");
        this.fingerprint = Utils.base64Decode((String)dictionary.get("fingerprint"));
        // check valid
        if (version == VersionMKM || version == VersionExBTC) {
            this.valid = key.verify(seed.getBytes("UTF-8"), fingerprint);
        } else { // VersionBTC
            // no seed, and the fingerprint is key.data
            this.valid = (seed == null) && (key != null) && (Arrays.equals(key.data, fingerprint));
        }
    }

    public Meta(byte version, PublicKey key, String seed, byte[] fingerprint) throws UnsupportedEncodingException {
        super();
        this.version = version;
        this.key = key;
        this.seed = seed;
        this.fingerprint = fingerprint;
        // check valid
        if (version == VersionMKM || version == VersionExBTC) {
            this.valid = key.verify(seed.getBytes("UTF-8"), fingerprint);
        } else { // VersionBTC
            // no seed, and the fingerprint is key.data
            this.valid = (seed == null) && (key != null) && (Arrays.equals(key.data, fingerprint));
        }
        dictionary.put("version", (int) version);
        dictionary.put("key", key);
        dictionary.put("seed", seed);
        dictionary.put("fingerprint", Utils.base64Encode(fingerprint));
    }

    public Meta(byte version, PrivateKey sk, String seed) throws UnsupportedEncodingException {
        this(version, sk.getPublicKey(), seed,
                version == VersionBTC ? sk.getPublicKey().data : sk.sign(seed.getBytes("UTF-8")));
    }

    @SuppressWarnings("unchecked")
    public static Meta getInstance(Object object) throws UnsupportedEncodingException, ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof Meta) {
            return (Meta) object;
        } else if (object instanceof Map) {
            return new Meta((Map<String, Object>) object);
        } else if (object instanceof String) {
            return new Meta(Utils.jsonDecode((String) object));
        } else {
            throw new IllegalArgumentException("unknown meta:" + object);
        }
    }

    public boolean equals(Map map) {
        return valid && dictionary.equals(map);
    }

    public boolean matches(PublicKey pk) {
        if (!valid) {
            return false;
        }
        if (version == VersionBTC) {
            // ID with BTC address has no username
            // so we can just compare the key.data to check matching
            return Arrays.equals(key.data, pk.data);
        }
        if (key.equals(pk)) {
            return true;
        }
        // check whether keys equal by verifying signature
        try {
            return pk.verify(seed.getBytes("UTF-8"), fingerprint);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean matches(ID identifier) {
        ID ident = buildID(identifier.getType());
        return ident != null && ident.equals(identifier);
    }

    public boolean matches(Address address) {
        Address addr = buildAddress(address.network);
        return addr != null && addr.equals(address);
    }

    public ID buildID(NetworkType network) {
        Address address = buildAddress(network);
        if (address == null) {
            return null;
        }
        if (version == VersionBTC) {
            return new ID(address);
        } else {
            // MKM & ExBTC
            return new ID(seed, address);
        }
    }

    public Address buildAddress(NetworkType network) {
        if (!valid) {
            return null;
        }
        if (version == VersionMKM) {
            return new Address(fingerprint, network);
        } else {
            // BTC & ExBTC
            return new Address(key.data, network);
        }
    }
}
