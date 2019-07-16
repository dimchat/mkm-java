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

import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
import chat.dim.crypto.impl.Dictionary;
import chat.dim.crypto.impl.PublicKeyImpl;
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

    protected Meta(Map<String, Object> dictionary) throws NoSuchFieldException, ClassNotFoundException {
        super(dictionary);
        this.version = (int) dictionary.get("version");
        this.key = PublicKeyImpl.getInstance(dictionary.get("key"));
        if (key == null) {
            throw new NoSuchFieldException("meta key error: " + dictionary);
        }
        // check valid
        if ((version & VersionMKM) == VersionMKM) { // MKM, ExBTC, ExETH, ...
            String name = (String) dictionary.get("seed");
            String base64 = (String) dictionary.get("fingerprint");
            if (name == null || base64 == null) {
                throw new NoSuchFieldException("meta error: " + dictionary);
            }
            byte[] signature = Base64.decode(base64);
            if (!key.verify(name.getBytes(Charset.forName("UTF-8")), signature)) {
                throw new ArithmeticException("fingerprint not match: " + dictionary);
            }
            this.seed = name;
            this.fingerprint = signature;
        } else {
            this.seed = null;
            this.fingerprint = null;
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
    public static Meta generate(int version, PrivateKey sk, String seed) throws ClassNotFoundException {
        Map<String, Object> dictionary = new HashMap<>();
        dictionary.put("version", version);
        dictionary.put("key", sk.getPublicKey());
        if ((version & VersionMKM) == VersionMKM) { // MKM, ExBTC, ExETH, ...
            // generate fingerprint with private key
            byte[] fingerprint = sk.sign(seed.getBytes(Charset.forName("UTF-8")));
            dictionary.put("seed", seed);
            dictionary.put("fingerprint", Base64.encode(fingerprint));
        }
        return Meta.getInstance(dictionary);
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
        return generateAddress(address.getNetwork()).equals(address);
    }

    public ID generateID(NetworkType network) {
        Address address = generateAddress(network);
        return new ID(seed, address, null);
    }

    /**
     *  Generate address with meta info and address type
     *
     * @param network - address network type
     * @return Address object
     */
    public abstract Address generateAddress(NetworkType network);

    //-------- Runtime --------

    private static Map<Integer, Class> metaClasses = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void register(int version, Class clazz) {
        // check whether clazz is subclass of Meta
        if (clazz.equals(Meta.class)) {
            throw new IllegalArgumentException("should not add Meta.class itself!");
        }
        clazz = clazz.asSubclass(Meta.class);
        metaClasses.put(version, clazz);
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
        // get subclass by meta version
        int version = (int) dictionary.get("version");
        Class clazz = metaClasses.get(version);
        if (clazz == null) {
            throw new ClassNotFoundException("meta not support: " + dictionary);
        }
        try {
            Constructor constructor = clazz.getConstructor(Map.class);
            return (Meta) constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    static {
        // MKM
        register(VersionMKM, DefaultMeta.class);
        // BTC, ExBTC
        // ETH, ExETH
        // ...
    }
}

/**
 *  Default Meta to build ID with 'name@address'
 *
 *  version:
 *      0x01 - MKM
 */
final class DefaultMeta extends Meta {

    public DefaultMeta(Map<String, Object> dictionary) throws NoSuchFieldException, ClassNotFoundException {
        super(dictionary);
    }

    @Override
    public Address generateAddress(NetworkType network) {
        assert version == VersionMKM;
        return BTCAddress.generate(fingerprint, network);
    }
}
