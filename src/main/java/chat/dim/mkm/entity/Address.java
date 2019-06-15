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

import chat.dim.crypto.Digest;
import chat.dim.format.Base58;

import java.util.Arrays;

/**
 *  Address like BitCoin
 *
 *      data format: "network+digest+checkcode"
 *          network    --  1 byte
 *          digest     -- 20 bytes
 *          check_code --  4 bytes
 *
 *      algorithm:
 *          fingerprint = sign(seed, SK);
 *          digest      = ripemd160(sha256(fingerprint));
 *          check_code  = sha256(sha256(network + digest)).prefix(4);
 *          address     = base58_encode(network + digest + check_code);
 */
public final class Address {

    public static final byte AlgorithmBTC     = 0x01;
    public static final byte AlgorithmETH     = 0x01;
    public static final byte AlgorithmDefault = AlgorithmBTC;

    private final String string;

    public final NetworkType network;
    public final long code;

    /**
     *  Copy address data
     *
     *  @param string - Encoded address string
     */
    public Address(String string) {
        this.string  = string;

        byte[] data = Base58.decode(string);
        if (data.length != 25) {
            throw new IndexOutOfBoundsException("address length error:" + data.length);
        }
        // Network ID
        NetworkType network = NetworkType.fromByte(data[0]);
        // Check Code
        byte[] prefix = new byte[21];
        byte[] suffix = new byte[4];
        System.arraycopy(data, 0, prefix, 0, 21);
        System.arraycopy(data, 21, suffix, 0, 4);
        byte[] cc = checkCode(prefix);
        if (Arrays.equals(cc, suffix)) {
            this.network = network;
            this.code    = userNumber(cc);
        } else {
            throw new ArithmeticException("address check code error:" + string);
        }
    }

    /**
     *  Generate address with fingerprint and network ID
     *
     *  @param fingerprint = sign(seed, PK)
     *  @param network - network ID
     */
    public Address(byte[] fingerprint, NetworkType network, byte algorithm) {
        if (algorithm == AlgorithmBTC) {
            // 1. digest = ripemd160(sha256(fingerprint))
            byte[] digest = Digest.ripemd160(Digest.sha256(fingerprint));
            // 2. head = network + digest
            byte[] head = new byte[21];
            head[0] = network.toByte();
            System.arraycopy(digest, 0, head, 1, 20);
            // 3. cc = sha256(sha256(_h)).prefix(4)
            byte[] cc = checkCode(head);
            // 4. data = base58_encode(_h + cc)
            byte[] data = new byte[25];
            System.arraycopy(head, 0, data, 0, 21);
            System.arraycopy(cc,0, data, 21, 4);

            this.string  = Base58.encode(data);
            this.network = network;
            this.code    = userNumber(cc);
        } else {
            throw new ArithmeticException("not support algorithm: " + algorithm);
        }
    }

    public static Address getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Address) {
            return (Address) object;
        } else if (object instanceof String) {
            return new Address((String) object);
        } else {
            throw new IllegalArgumentException("unknown address:" + object);
        }
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            // same object
            return true;
        } else if (other instanceof Address) {
            // check with inner string
            Address address = (Address) other;
            return string.equals(address.string);
        } else if (other instanceof String) {
            // same string
            return string.equals(other);
        } else {
            // null or unknown object
            return false;
        }
    }

    @Override
    public String toString() {
        return string;
    }

    private static byte[] checkCode(byte[] data) {
        byte[] sha256d = Digest.sha256(Digest.sha256(data));
        assert sha256d != null;
        byte[] cc = new byte[4];
        System.arraycopy(sha256d, 0, cc, 0, 4);
        return cc;
    }

    private static long userNumber(byte[] cc) {
        return (long)(cc[3] & 0xFF) << 24 | (cc[2] & 0xFF) << 16 | (cc[1] & 0xFF) << 8 | (cc[0] & 0xFF);
    }
}
