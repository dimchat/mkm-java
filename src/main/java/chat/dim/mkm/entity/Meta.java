/* license: https://mit-license.org
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
package chat.dim.mkm.entity;

import chat.dim.crypto.Dictionary;
import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
import chat.dim.format.Base64;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.HashMap;
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
    public final int version;

    /**
     *  Public key (used for signature)
     *
     *      RSA / ECC
     */
    public final PublicKey key;

    /**
     *  Seed to generate fingerprint
     *
     *      Username / Group-X
     */
    public final String seed;

    /**
     *  Fingerprint to verify ID and public key
     *
     *      Build: fingerprint = sign(seed, privateKey)
     *      Check: verify(seed, fingerprint, publicKey)
     */
    public final byte[] fingerprint;

    public Meta(Map<String, Object> dictionary) throws ClassNotFoundException {
        super(dictionary);
        this.version     = (int) dictionary.get("version");
        this.key         = PublicKey.getInstance(dictionary.get("key"));
        // check valid
        if ((version & VersionMKM) == VersionMKM) { // MKM, ExBTC, ExETH, ...
            String seed = (String) dictionary.get("seed");
            String base64 = (String) dictionary.get("fingerprint");
            byte[] fingerprint = Base64.decode(base64);
            if (!key.verify(seed.getBytes(Charset.forName("UTF-8")), fingerprint)) {
                throw new ArithmeticException("fingerprint not match: " + dictionary);
            }
            this.seed = seed;
            this.fingerprint = fingerprint;
        } else {
            this.seed = null;
            this.fingerprint = null;
        }
    }

    /**
     *  Copy meta data
     *
     * @param version - meta algorithm version
     * @param key - public key
     * @param seed - user/group name
     * @param fingerprint - signature of seed, or key data
     */
    private Meta(int version, PublicKey key, String seed, byte[] fingerprint) {
        super();
        this.version = version;
        this.key = key;
        this.seed = seed;
        this.fingerprint = fingerprint;
        dictionary.put("version", version);
        dictionary.put("key", key);
        if ((version & VersionMKM) == VersionMKM) { // MKM, ExBTC, ExETH, ...
            // check valid
            if (!key.verify(seed.getBytes(Charset.forName("UTF-8")), fingerprint)) {
                throw new ArithmeticException("fingerprint not match: " + dictionary);
            }
            dictionary.put("seed", seed);
            dictionary.put("fingerprint", Base64.encode(fingerprint));
        }
    }

    /**
     *  Generate meta info with seed and private key
     *
     * @param version - meta version(MKM, BTC, ETH, ...)
     * @param sk - private key
     * @param seed - user/group name
     * @return Meta object
     */
    public static Meta generate(int version, PrivateKey sk, String seed) {
        if ((version & VersionMKM) == VersionMKM) { // MKM, ExBTC, ExETH, ...
            // generate fingerprint with private key
            byte[] fingerprint = sk.sign(seed.getBytes(Charset.forName("UTF-8")));
            return new Meta(version, sk.getPublicKey(), seed, fingerprint);
        } else { // BTC, ETH, ...
            return new Meta(version, sk.getPublicKey(), null, null);
        }
    }

    public boolean matches(PublicKey pk) {
        if (key.equals(pk)) {
            return true;
        }
        if ((version & VersionMKM) == VersionMKM) { // MKM, ExBTC, ExETH, ...
            // check whether keys equal by verifying signature
            return pk.verify(seed.getBytes(Charset.forName("UTF-8")), fingerprint);
        } else { // BTC, ETH, ...
            // ID with BTC/ETH address has no username
            // so we can just compare the key.data to check matching
            return false;
        }
    }

    public boolean matches(ID identifier) {
        if (seed == null) {
            String name = identifier.name;
            if (name != null && name.length() > 0) {
                return false;
            }
        } else if (!seed.equals(identifier.name)) {
            return false;
        }
        return matches(identifier.address);
    }

    public boolean matches(Address address) {
        return generateAddress(address.getType()).equals(address);
    }

    public ID generateID(NetworkType network) {
        Address address = generateAddress(network);
        return new ID(seed, address, null);
    }

    public Address generateAddress(NetworkType network) {
        if (version != VersionMKM) {
            throw new ArithmeticException("meta version error");
        }
        // MKM
        return BTCAddress.generate(fingerprint, network);
    }

    //-------- Runtime --------

    private static Map<Integer, Class> metaClasses = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void register(int version, Class clazz) {
        // check whether clazz is subclass of Meta
        clazz = clazz.asSubclass(Meta.class);
        metaClasses.put(version, clazz);
    }

    @SuppressWarnings("unchecked")
    private static Meta createInstance(Map<String, Object> dictionary)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        int version = (int) dictionary.get("version");
        Class clazz = metaClasses.get(version);
        if (clazz == null) {
            // default meta
            throw new ClassNotFoundException("unknown meta version: " + version);
        }
        Constructor constructor = clazz.getConstructor(Map.class);
        return (Meta) constructor.newInstance(dictionary);
    }

    @SuppressWarnings("unchecked")
    public static Meta getInstance(Object object)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (object == null) {
            return null;
        } else if (object instanceof Meta) {
            return (Meta) object;
        } else if (object instanceof Map) {
            return createInstance((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("meta error: " + object);
        }
    }

    static {
        // MKM
        register(VersionMKM, Meta.class); // default
        // BTC, ExBTC
        register(VersionBTC, BTCMeta.class);
        register(VersionExBTC, BTCMeta.class);
        // ETH, ExETH
        // ...
    }
}

final class BTCMeta extends Meta {

    public BTCMeta(Map<String, Object> dictionary) throws ClassNotFoundException {
        super(dictionary);
    }

    public Address generateAddress(NetworkType network) {
        if ((version & VersionBTC) != VersionBTC) {
            throw new ArithmeticException("meta version error");
        }
        // BTC, ExBTC
        return BTCAddress.generate(key.data, network);
    }
}
