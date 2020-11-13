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

import chat.dim.crypto.VerifyKey;

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
public interface Meta extends Map<String, Object> {

    Map<String, Object> getMap();
    Map<String, Object> copyMap();

    /**
     *  Meta algorithm version
     *
     *      0x01 - username@address
     *      0x02 - btc_address
     *      0x03 - username@btc_address
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
     *  Check meta valid
     *  (must call this when received a new meta from network)
     *
     * @return true on valid
     */
    boolean isValid();

    /**
     *  Check whether meta match with entity ID
     *  (must call this when received a new meta from network)
     *
     * @param identifier - entity ID
     * @return true on matched
     */
    boolean matches(ID identifier);

    boolean matches(VerifyKey pk);

    /**
     *  Meta Parser
     *  ~~~~~~~~~~~
     */
    interface Parser {

        /**
         *  Parse map object to meta
         *
         * @param meta - meta info
         * @return Meta
         */
        Meta parseMeta(Map<String, Object> meta);
    }
}
