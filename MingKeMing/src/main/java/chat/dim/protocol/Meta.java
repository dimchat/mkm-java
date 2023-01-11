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
import chat.dim.mkm.FactoryManager;
import chat.dim.type.Mapper;

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
public interface Meta extends Mapper {

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
     * @param network - address type
     * @return Address
     */
    Address generateAddress(int network);

    /**
     *  Check meta valid
     *  (must call this when received a new meta from network)
     *
     * @return true on valid
     */
    static boolean check(Meta meta) {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.checkMeta(meta);
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
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.matches(identifier, meta);
    }

    /**
     *  Check whether meta match with public key
     *
     * @param pk   - public key
     * @param meta - meta info
     * @return true on matched
     */
    static boolean matches(VerifyKey pk, Meta meta) {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.matches(pk, meta);
    }

    //
    //  Factory methods
    //
    static Meta create(int version, VerifyKey key, String seed, byte[] fingerprint) {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.createMeta(version, key, seed, fingerprint);
    }
    static Meta generate(int version, SignKey sKey, String seed) {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.generateMeta(version, sKey, seed);
    }
    static Meta parse(Object meta) {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.parseMeta(meta);
    }
    static void setFactory(int version, Factory factory) {
        FactoryManager man = FactoryManager.getInstance();
        man.generalFactory.metaFactories.put(version, factory);
    }
    static void setFactory(MetaType version, Factory factory) {
        FactoryManager man = FactoryManager.getInstance();
        man.generalFactory.metaFactories.put(version.value, factory);
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
