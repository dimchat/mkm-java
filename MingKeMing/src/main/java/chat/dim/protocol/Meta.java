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
import chat.dim.format.TransportableData;
import chat.dim.mkm.AccountFactoryManager;
import chat.dim.type.Mapper;

/**
 *  User/Group Meta data
 *  ~~~~~~~~~~~~~~~~~~~~
 *  This class is used to generate entity meta
 *
 *      data format: {
 *          type: 1,             // algorithm version
 *          key: "{public key}", // PK = secp256k1(SK);
 *          seed: "moKy",        // user/group name
 *          fingerprint: "..."   // CT = sign(seed, SK);
 *      }
 *
 *      algorithm:
 *          fingerprint = sign(seed, SK);
 */
public interface Meta extends Mapper {

    /**
     *  MetaType
     *  ~~~~~~~~
     *  Meta algorithm names
     */
    String MKM = "MKM"; // "1";
    String BTC = "BTC"; // "2";
    String ETH = "ETH"; // "4";
    // ...

    /**
     *  Meta algorithm version
     *
     *      1 = MKM : username@address (default)
     *      2 = BTC : btc_address
     *      4 = ETH : eth_address
     *      ...
     */
    String getType();

    /**
     *  Public key (used for signature)
     *
     *      RSA / ECC
     */
    VerifyKey getPublicKey();

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

    //
    //  Validation
    //

    /**
     *  Check meta valid
     *  (must call this when received a new meta from network)
     *
     * @return true on valid
     */
    boolean isValid();

    /**
     *  Check whether meta matches with entity ID
     *  (must call this when received a new meta from network)
     *
     * @param identifier - entity ID
     * @return true on matched
     */
    boolean matchIdentifier(ID identifier);

    /**
     *  Check whether meta matches with public key
     *
     * @param pKey - public key
     * @return true on matched
     */
    boolean matchPublicKey(VerifyKey pKey);

    //
    //  Factory methods
    //

    /**
     *  Create from stored info
     */
    static Meta create(String type, VerifyKey pKey, String seed, TransportableData fingerprint) {
        AccountFactoryManager man = AccountFactoryManager.getInstance();
        return man.generalFactory.createMeta(type, pKey, seed, fingerprint);
    }

    /**
     *  Generate with private key
     */
    static Meta generate(String type, SignKey sKey, String seed) {
        AccountFactoryManager man = AccountFactoryManager.getInstance();
        return man.generalFactory.generateMeta(type, sKey, seed);
    }

    static Meta parse(Object meta) {
        AccountFactoryManager man = AccountFactoryManager.getInstance();
        return man.generalFactory.parseMeta(meta);
    }

    static Factory getFactory(String type) {
        AccountFactoryManager man = AccountFactoryManager.getInstance();
        return man.generalFactory.getMetaFactory(type);
    }
    static void setFactory(String type, Factory factory) {
        AccountFactoryManager man = AccountFactoryManager.getInstance();
        man.generalFactory.setMetaFactory(type, factory);
    }

    /**
     *  Meta Factory
     *  ~~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Create meta
         *
         * @param pKey        - public key
         * @param seed        - ID.name
         * @param fingerprint - sKey.sign(seed)
         * @return Meta
         */
        Meta createMeta(VerifyKey pKey, String seed, TransportableData fingerprint);

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
