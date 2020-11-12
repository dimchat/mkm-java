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
package chat.dim.mkm;

import java.util.Map;

import chat.dim.crypto.PublicKey;
import chat.dim.crypto.VerifyKey;
import chat.dim.format.Base64;
import chat.dim.format.UTF8;
import chat.dim.protocol.Meta;
import chat.dim.protocol.MetaType;
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
public abstract class BaseMeta extends Dictionary<String, Object> implements Meta {

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
    private VerifyKey key = null;

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

    protected BaseMeta(Map<String, Object> dictionary) {
        super(dictionary);
    }

    @Override
    public int getType() {
        if (version == 0) {
            version = (int) get("version");
        }
        return version;
    }

    @SuppressWarnings("unchecked")
    @Override
    public VerifyKey getKey() {
        if (key == null) {
            try {
                Object info = get("key");
                if (info instanceof Map) {
                    key = PublicKey.getInstance((Map<String, Object>) info);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return key;
    }

    @Override
    public String getSeed() {
        if (seed == null) {
            if (MetaType.hasSeed(getType())) {
                seed = (String) get("seed");
                assert seed != null && seed.length() > 0 : "meta.seed should not be empty: " + this;
            }
        }
        return seed;
    }

    @Override
    public byte[] getFingerprint() {
        if (fingerprint == null) {
            if (MetaType.hasSeed(getType())) {
                String base64 = (String) get("fingerprint");
                assert base64 != null && base64.length() > 0 : "meta.fingerprint should not be empty: " + this;
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
    @Override
    public boolean isValid() {
        if (status == 0) {
            VerifyKey key = getKey();
            if (key == null) {
                // meta.key should not be empty
                status = -1;
            } else if (MetaType.hasSeed(getType())) {
                String seed = getSeed();
                byte[] fingerprint = getFingerprint();
                if (seed == null || fingerprint == null) {
                    // seed and fingerprint should not be empty
                    status = -1;
                } else if (key.verify(UTF8.encode(seed), fingerprint)) {
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

    @Override
    public boolean matches(VerifyKey pk) {
        if (!isValid()) {
            return false;
        }
        // check whether the public key equals to meta.key
        if (pk.equals(getKey())) {
            return true;
        }
        // check with seed & fingerprint
        if (MetaType.hasSeed(getType())) {
            // check whether keys equal by verifying signature
            String seed = getSeed();
            byte[] fingerprint = getFingerprint();
            return pk.verify(UTF8.encode(seed), fingerprint);
        } else {
            // ID with BTC/ETH address has no username
            // so we can just compare the key.data to check matching
            return false;
        }
    }
}