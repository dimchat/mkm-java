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

import chat.dim.crypto.PublicKey;
import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;
import chat.dim.format.Base64;
import chat.dim.mkm.Factories;

/**
 *  User/Group Meta data
 *  ~~~~~~~~~~~~~~~~~~~~
 *  This class is used to generate entity meta
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
public interface Meta extends chat.dim.type.Map {

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

    @SuppressWarnings("unchecked")
    static VerifyKey getKey(Map<String, Object> meta) {
        Object key = meta.get("key");
        if (key instanceof Map) {
            return PublicKey.parse((Map<String, Object>) key);
        }
        throw new NullPointerException("meta key not found: " + meta);
    }

    /**
     *  Seed to generate fingerprint
     *
     *      Username / Group-X
     */
    String getSeed();

    static String getSeed(Map<String, Object> meta) {
        return (String) meta.get("seed");
    }

    /**
     *  Fingerprint to verify ID and public key
     *
     *      Build: fingerprint = sign(seed, privateKey)
     *      Check: verify(seed, fingerprint, publicKey)
     */
    byte[] getFingerprint();

    static byte[] getFingerprint(Map<String, Object> meta) {
        String base64 = (String) meta.get("fingerprint");
        if (base64 == null) {
            return null;
        }
        return Base64.decode(base64);
    }

    /**
     *  Check meta valid
     *  (must call this when received a new meta from network)
     *
     * @return true on valid
     */
    boolean isValid();

    /**
     *  Generate ID with terminal
     *
     * @param type - ID.type
     * @param terminal - ID.terminal
     * @return ID
     */
    ID generateID(byte type, String terminal);

    /**
     *  Check whether meta match with entity ID
     *  (must call this when received a new meta from network)
     *
     * @param identifier - entity ID
     * @return true on matched
     */
    boolean matches(ID identifier);

    /**
     *  Check whether meta match with public key
     *
     * @param pk - public key
     * @return true on matched
     */
    boolean matches(VerifyKey pk);

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
        } else if (meta instanceof chat.dim.type.Map) {
            meta = ((chat.dim.type.Map) meta).getMap();
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
