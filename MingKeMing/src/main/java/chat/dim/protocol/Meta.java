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
package chat.dim.protocol;

import java.util.Map;

import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;
import chat.dim.format.UTF8;
import chat.dim.mkm.Factories;
import chat.dim.type.MapWrapper;

/**
 *  User/Group Meta data
 *  ~~~~~~~~~~~~~~~~~~~~
 *  This class is used to generate entity meta
 *
 *      data format: {
 *          type: 1,             // algorithm version
 *          seed: "moKy",        // user/group name
 *          key: "{public key}", // PK = secp256k1(SK);
 *          fingerprint: "..."   // CT = sign(seed, SK);
 *      }
 *
 *      algorithm:
 *          fingerprint = sign(seed, SK);
 */
public interface Meta extends MapWrapper {

    /**
     *  Meta algorithm version
     *
     *      0x01 - username@address
     *      0x02 - btc_address
     *      0x03 - username@btc_address
     *      0x04 - eth_address
     *      0x05 - username@eth_address
     */
    int getType();

    static int getType(Map<String, Object> meta) {
        Object version = meta.get("type");
        if (version == null) {
            // compatible with v1.0
            version = meta.get("version");
            if (version == null) {
                throw new NullPointerException("meta type not found: " + meta);
            }
        }
        return ((Number) version).intValue();
    }

    /**
     *  Public key (used for signature)
     *
     *      RSA / ECC
     */
    VerifyKey getKey();

    /**
     *  Seed to generate fingerprint
     *
     *      Username / Group-X
     */
    String getSeed();

    /**
     *  Fingerprint to verify ID and public key
     *
     *      Build: fingerprint = sign(seed, privateKey)
     *      Check: verify(seed, fingerprint, publicKey)
     */
    byte[] getFingerprint();

    /**
     *  Generate address
     *
     * @param type - Address.network
     * @return Address
     */
    Address generateAddress(byte type);

    /**
     *  Check meta valid
     *  (must call this when received a new meta from network)
     *
     * @return true on valid
     */
    static boolean check(Meta meta) {
        VerifyKey key = meta.getKey();
        if (key == null) {
            // meta.key should not be empty
            return false;
        }
        if (!MetaType.hasSeed(meta.getType())) {
            // this meta has no seed, so no signature too
            return true;
        }
        // check seed with signature
        String seed = meta.getSeed();
        byte[] fingerprint = meta.getFingerprint();
        if (seed == null || fingerprint == null) {
            // seed and fingerprint should not be empty
            return false;
        }
        // verify fingerprint
        return key.verify(UTF8.encode(seed), fingerprint);
    }

    /**
     *  Check whether meta match with entity ID
     *  (must call this when received a new meta from network)
     *
     * @param identifier - entity ID
     * @param meta       - entity meta
     * @return true on matched
     */
    static boolean matches(ID identifier, Meta meta) {
        // check ID.name
        String seed = meta.getSeed();
        String name = identifier.getName();
        if (name == null || name.length() == 0) {
            if (seed != null && seed.length() > 0) {
                return false;
            }
        } else if (!name.equals(seed)) {
            return false;
        }
        // check ID.address
        Address address = Address.generate(meta, identifier.getType());
        return identifier.getAddress().equals(address);
    }

    /**
     *  Check whether meta match with public key
     *
     * @param pk   - public key
     * @param meta - meta info
     * @return true on matched
     */
    static boolean matches(VerifyKey pk, Meta meta) {
        // check whether the public key equals to meta.key
        if (pk.equals(meta.getKey())) {
            return true;
        }
        // check with seed & fingerprint
        if (MetaType.hasSeed(meta.getType())) {
            // check whether keys equal by verifying signature
            String seed = meta.getSeed();
            byte[] fingerprint = meta.getFingerprint();
            return pk.verify(UTF8.encode(seed), fingerprint);
        } else {
            // ID with BTC/ETH address has no username
            // so we can just compare the key.data to check matching
            return false;
        }
    }

    //
    //  Factory methods
    //
    static Meta create(int type, VerifyKey key, String seed, byte[] fingerprint) {
        Factory factory = getFactory(type);
        if (factory == null) {
            throw new NullPointerException("meta type not found: " + type);
        }
        return factory.createMeta(key, seed, fingerprint);
    }
    static Meta generate(int type, SignKey sKey, String seed) {
        Factory factory = getFactory(type);
        if (factory == null) {
            throw new NullPointerException("meta type not found: " + type);
        }
        return factory.generateMeta(sKey, seed);
    }
    static Meta parse(Map<String, Object> meta) {
        if (meta == null) {
            return null;
        } else if (meta instanceof Meta) {
            return (Meta) meta;
        } else if (meta instanceof MapWrapper) {
            meta = ((MapWrapper) meta).getMap();
        }
        int type = getType(meta);
        Factory factory = getFactory(type);
        if (factory == null) {
            factory = getFactory(0);  // unknown
            assert factory != null : "cannot parse entity meta: " + meta;
        }
        return factory.parseMeta(meta);
    }

    static Factory getFactory(int type) {
        return Factories.metaFactories.get(type);
    }
    static Factory getFactory(MetaType type) {
        return Factories.metaFactories.get(type.value);
    }
    static void register(int type, Factory factory) {
        Factories.metaFactories.put(type, factory);
    }
    static void register(MetaType type, Factory factory) {
        Factories.metaFactories.put(type.value, factory);
    }

    /**
     *  Meta Factory
     *  ~~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Create meta
         *
         * @param key         - public key
         * @param seed        - ID.name
         * @param fingerprint - sKey.sign(seed)
         * @return Meta
         */
        Meta createMeta(VerifyKey key, String seed, byte[] fingerprint);

        /**
         *  Generate meta
         *
         * @param sKey    - private key
         * @param seed    - ID.name
         * @return Meta
         */
        Meta generateMeta(SignKey sKey, String seed);

        /**
         *  Parse map object to meta
         *
         * @param meta - meta info
         * @return Meta
         */
        Meta parseMeta(Map<String, Object> meta);
    }
}
