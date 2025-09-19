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

import chat.dim.ext.SharedAccountExtensions;
import chat.dim.type.Mapper;

/**
 *  User/Group Meta data
 *  <p>
 *      This class is used to generate entity meta
 *  </p>
 *
 *  <blockquote><pre>
 *  data format: {
 *      type        : 1,              // algorithm version
 *      key         : "{public key}", // PK = secp256k1(SK);
 *      seed        : "moKy",         // user/group name
 *      fingerprint : "..."           // CT = sign(seed, SK);
 *  }
 *
 *  algorithm:
 *      fingerprint = sign(seed, SK);
 *  </pre></blockquote>
 */
public interface Meta extends Mapper {

    /**
     *  Meta algorithm version
     *
     *  <pre>
     *  1 = MKM : username@address (default)
     *  2 = BTC : btc_address
     *  4 = ETH : eth_address
     *  ...
     *  </pre>
     */
    String getType();

    /**
     *  Public key (used for signature)
     *
     *  <p>RSA / ECC</p>
     */
    VerifyKey getPublicKey();

    /**
     *  Seed to generate fingerprint
     *
     *  <p>Username / Group-X</p>
     */
    String getSeed();

    /**
     *  Fingerprint to verify ID and public key
     *
     *  <blockquote><pre>
     *  Build: fingerprint = sign(seed, privateKey)
     *  Check: verify(seed, fingerprint, publicKey)
     *  </pre></blockquote>
     */
    byte[] getFingerprint();

    //
    //  Validation
    //

    /**
     *  Check meta valid
     *  <p>(must call this when received a new meta from network)</p>
     *
     * @return true on valid
     */
    boolean isValid();

    /**
     *  Generate address
     *
     * @param network - address type
     * @return Address
     */
    Address generateAddress(int network);

    //
    //  Factory methods
    //

    /**
     *  Create from stored info
     */
    static Meta create(String type, VerifyKey pKey, String seed, TransportableData fingerprint) {
        return SharedAccountExtensions.metaHelper.createMeta(type, pKey, seed, fingerprint);
    }

    /**
     *  Generate with private key
     */
    static Meta generate(String type, SignKey sKey, String seed) {
        return SharedAccountExtensions.metaHelper.generateMeta(type, sKey, seed);
    }

    static Meta parse(Object meta) {
        return SharedAccountExtensions.metaHelper.parseMeta(meta);
    }

    static Factory getFactory(String type) {
        return SharedAccountExtensions.metaHelper.getMetaFactory(type);
    }
    static void setFactory(String type, Factory factory) {
        SharedAccountExtensions.metaHelper.setMetaFactory(type, factory);
    }

    /**
     *  Meta Factory
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
