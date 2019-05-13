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

import chat.dim.crypto.Base64;
import chat.dim.crypto.Dictionary;
import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;

import java.nio.charset.StandardCharsets;
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
public final class Meta extends Dictionary {

    /**
     *  Meta algorithm version
     *
     *      0x01 - username@address
     *      0x02 - btc_address
     *      0x03 - username@btc_address
     */
    public final byte version;

    /**
     *  Public key
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

    public final boolean valid;

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
    public static final byte VersionMKM     = 0x01;
    public static final byte VersionBTC     = 0x02;
    public static final byte VersionExBTC   = 0x03;
    public static final byte VersionDefault = VersionMKM;

    public Meta(Map<String, Object> dictionary) throws ClassNotFoundException {
        super(dictionary);
        this.version     = Integer.getInteger((String)dictionary.get("version")).byteValue();
        this.key         = PublicKey.getInstance(dictionary.get("key"));
        this.seed        = (String)dictionary.get("seed");
        this.fingerprint = Base64.decode((String)dictionary.get("fingerprint"));
        // check valid
        if (version == VersionMKM || version == VersionExBTC) {
            this.valid = key.verify(seed.getBytes(StandardCharsets.UTF_8), fingerprint);
        } else { // VersionBTC
            // no seed, and the fingerprint is key.data
            this.valid = (seed == null) && (key != null) && (Arrays.equals(key.data, fingerprint));
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
    public Meta(byte version, PublicKey key, String seed, byte[] fingerprint) {
        super();
        this.version = version;
        this.key = key;
        this.seed = seed;
        this.fingerprint = fingerprint;
        // check valid
        if (version == VersionMKM || version == VersionExBTC) {
            this.valid = key.verify(seed.getBytes(StandardCharsets.UTF_8), fingerprint);
        } else { // VersionBTC
            // no seed, and the fingerprint is key.data
            this.valid = (seed == null) && (key != null) && (Arrays.equals(key.data, fingerprint));
        }
        dictionary.put("version", (int) version);
        dictionary.put("key", key);
        dictionary.put("seed", seed);
        dictionary.put("fingerprint", Base64.encode(fingerprint));
    }

    /**
     * Generate fingerprint, initialize meta data
     *
     * @param version - meta version for MKM or ExBTC
     * @param sk - private key to generate fingerprint
     * @param seed - seed for fingerprint
     */
    public Meta(byte version, PrivateKey sk, String seed) {
        this(version, sk.getPublicKey(), seed,
                version == VersionBTC ? sk.getPublicKey().data : sk.sign(seed.getBytes(StandardCharsets.UTF_8)));
    }

    public Meta(PrivateKey sk, String seed) {
        this(VersionDefault, sk, seed);
    }

    /**
     *  For BTC address
     *
     * @param key - public key (ECC)
     */
    public Meta(PublicKey key) {
        this(VersionBTC, key, null, key.data);
    }

    @SuppressWarnings("unchecked")
    public static Meta getInstance(Object object) throws ClassNotFoundException {
        if (object == null) {
            return null;
        } else if (object instanceof Meta) {
            return (Meta) object;
        } else if (object instanceof Map) {
            return new Meta((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("unknown meta:" + object);
        }
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
        return pk.verify(seed.getBytes(StandardCharsets.UTF_8), fingerprint);
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
