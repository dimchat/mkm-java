/* license: https://mit-license.org
 *
 *  Ming-Ke-Ming : Decentralized User Identity Authentication
 *
 *                                Written in 2019 by Moky <albert.moky@gmail.com>
 *
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Albert Moky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ==============================================================================
 */
package chat.dim;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
import chat.dim.format.Base64;
import chat.dim.impl.PublicKeyImpl;
import chat.dim.plugins.DefaultMeta;
import chat.dim.type.Dictionary;

/**
 *  User/Group Meta data
 *  ~~~~~~~~~~~~~~~~~~~~
 *  This class is used to generate entity ID
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
 */
public abstract class Meta extends Dictionary {

    /**
     *  enum MKMMetaVersion
     *
     *  abstract Defined for algorithm that generating address.
     *
     *  discussion Generate and check ID/Address
     *
     *      MKMMetaVersion_MKM give a seed string first, and sign this seed to get
     *      fingerprint; after that, use the fingerprint to generate address.
     *      This will get a firmly relationship between (username, address and key).
     *
     *      MKMMetaVersion_BTC use the key data to generate address directly.
     *      This can build a BTC address for the entity ID (no username).
     *
     *      MKMMetaVersion_ExBTC use the key data to generate address directly, and
     *      sign the seed to get fingerprint (just for binding username and key).
     *      This can build a BTC address, and bind a username to the entity ID.
     */
    public static final int VersionMKM     = 0x01;  // 0000 0001
    public static final int VersionBTC     = 0x02;  // 0000 0010
    public static final int VersionExBTC   = 0x03;  // 0000 0011
    public static final int VersionETH     = 0x04;  // 0000 0100
    public static final int VersionExETH   = 0x05;  // 0000 0101
    public static final int VersionDefault = VersionMKM;

    /**
     *  Meta algorithm version
     *
     *      0x01 - username@address
     *      0x02 - btc_address
     *      0x03 - username@btc_address
     */
    private int version = 0;

    /**
     *  Public key (used for signature)
     *
     *      RSA / ECC
     */
    private PublicKey key = null;

    /**
     *  Seed to generate fingerprint
     *
     *      Username / Group-X
     */
    private String seed = null;

    /**
     *  Fingerprint to verify ID and public key
     *
     *      Build: fingerprint = sign(seed, privateKey)
     *      Check: verify(seed, fingerprint, publicKey)
     */
    private byte[] fingerprint = null;

    private int status = 0;  // 1 for valid, -1 for invalid

    protected Meta(Map<String, Object> dictionary) {
        super(dictionary);
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            // same dictionary
            return true;
        }
        Meta meta;
        if (other instanceof Meta) {
            meta = (Meta) other;
        } else if (other instanceof Map) {
            try {
                meta = Meta.getInstance(other);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            assert other == null; // unsupported type
            return false;
        }
        ID identifier = meta.generateID(NetworkType.Main);
        return matches(identifier);
    }

    public int getVersion() {
        if (version == 0) {
            version = (int) dictionary.get("version");
        }
        return version;
    }

    private boolean containsSeed() {
        return containsSeed(getVersion());
    }

    private static boolean containsSeed(int version) {
        // MKM, ExBTC, ExETH, ...
        return (version & VersionMKM) == VersionMKM;
    }

    public PublicKey getKey() {
        if (key == null) {
            try {
                key = PublicKeyImpl.getInstance(dictionary.get("key"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return key;
    }

    public String getSeed() {
        if (seed == null) {
            if (containsSeed()) {
                seed = (String) dictionary.get("seed");
                assert seed != null && seed.length() > 0;
            }
        }
        return seed;
    }

    public byte[] getFingerprint() {
        if (fingerprint == null) {
            if (containsSeed()) {
                String base64 = (String) dictionary.get("fingerprint");
                assert base64 != null && base64.length() > 0;
                fingerprint = Base64.decode(base64);
            }
        }
        return fingerprint;
    }

    /**
     *  Check meta valid
     *  (must call this when received a new meta from network)
     *
     * @return true on valid
     */
    public boolean isValid() {
        if (status == 0) {
            PublicKey key = getKey();
            if (key == null) {
                // meta.key should not be empty
                status = -1;
            } else if (containsSeed()) {
                String seed = getSeed();
                byte[] fingerprint = getFingerprint();
                if (seed == null || fingerprint == null) {
                    // seed and fingerprint should not be empty
                    status = -1;
                } else if (key.verify(seed.getBytes(Charset.forName("UTF-8")), fingerprint)) {
                    // fingerprint matched
                    status = 1;
                } else {
                    // fingerprint not matched
                    status = -1;
                }
            } else {
                status = 1;
            }
        }
        return status == 1;
    }

    public boolean matches(PublicKey pk) {
        // check whether the public key equals to meta.key
        if (pk.equals(getKey())) {
            return true;
        }
        // check with seed & fingerprint
        if (containsSeed()) {
            // check whether keys equal by verifying signature
            return pk.verify(seed.getBytes(Charset.forName("UTF-8")), fingerprint);
        } else {
            // ID with BTC/ETH address has no username
            // so we can just compare the key.data to check matching
            return false;
        }
    }

    /**
     *  Check whether meta match with entity ID
     *  (must call this when received a new meta from network)
     *
     * @param identifier - entity ID
     * @return true on matched
     */
    public boolean matches(ID identifier) {
        return generateID(identifier.getType()).equals(identifier);
    }

    public boolean matches(Address address) {
        return generateAddress(address.getNetwork()).equals(address);
    }

    public ID generateID(NetworkType network) {
        Address address = generateAddress(network);
        return new ID(getSeed(), address, null);
    }

    /**
     *  Generate address with meta info and address type
     *
     * @param network - address network type
     * @return Address object
     */
    public abstract Address generateAddress(NetworkType network);

    /**
     *  Generate meta info with seed and private key
     *
     * @param version - meta version(MKM, BTC, ETH, ...)
     * @param sk - private key
     * @param seed - user/group name
     * @return Meta object
     */
    public static Meta generate(int version, PrivateKey sk, String seed) throws ClassNotFoundException {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("version", version);
        dictionary.put("key", sk.getPublicKey());
        if (containsSeed(version)) {
            // generate fingerprint with private key
            byte[] fingerprint = sk.sign(seed.getBytes(Charset.forName("UTF-8")));
            dictionary.put("seed", seed);
            dictionary.put("fingerprint", Base64.encode(fingerprint));
        }
        return Meta.getInstance(dictionary);
    }

    //-------- Runtime --------

    private static Map<Integer, Class> metaClasses = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void register(int version, Class clazz) {
        // check whether clazz is subclass of Meta
        if (clazz.equals(Meta.class)) {
            throw new IllegalArgumentException("should not add Meta.class itself!");
        }
        assert Meta.class.isAssignableFrom(clazz); // asSubclass
        metaClasses.put(version, clazz);
    }

    private static Class metaClass(Map<String, Object> dictionary) {
        // get subclass by meta version
        int version = (int) dictionary.get("version");
        return metaClasses.get(version);
    }

    @SuppressWarnings("unchecked")
    public static Meta getInstance(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof Meta) {
            return (Meta) object;
        }
        assert object instanceof Map;
        Map<String, Object> dictionary = (Map<String, Object>) object;
        Class clazz = metaClass(dictionary);
        if (clazz == null) {
            throw new ClassNotFoundException("meta not support: " + dictionary);
        }
        // create instance by subclass (with meta version)
        return (Meta) createInstance(clazz, dictionary);
    }

    static {
        // MKM
        register(VersionMKM, DefaultMeta.class);
        // BTC, ExBTC
        // ETH, ExETH
        // ...
    }
}
