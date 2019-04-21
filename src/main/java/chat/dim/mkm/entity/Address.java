package chat.dim.mkm.entity;

import chat.dim.crypto.Utils;

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
public class Address {

    private final String string;

    public final NetworkType network;
    public final long code;
    public final boolean valid;

    public Address(Address address) {
        this.string  = address.string;
        this.network = address.network;
        this.code    = address.code;
        this.valid   = address.valid;
    }

    public Address(String string) {
        this.string  = string;

        byte[] data = Utils.base58Decode(string);
        if (data.length == 25) {
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
                this.valid   = true;
            } else {
                this.network = null;
                this.code    = 0;
                this.valid   = false;
            }
        } else {
            this.network = null;
            this.code    = 0;
            this.valid   = false;
        }
    }

    public Address(byte[] fingerprint, NetworkType network) {
        /**
         *  BTC address algorithm:
         *      digest     = ripemd160(sha256(fingerprint));
         *      check_code = sha256(sha256(network + digest)).prefix(4);
         *      addr       = base58_encode(network + digest + check_code);
         */

        // 1. hash = ripemd160(sha256(CT))
        byte[] hash = Utils.ripemd160(Utils.sha256(fingerprint));
        // 2. _h = network + hash
        byte[] h = new byte[21];
        h[0] = network.toByte();
        System.arraycopy(hash, 0, h, 1, 20);
        // 3. cc = sha256(sha256(_h)).prefix(4)
        byte[] cc = checkCode(h);
        // 4. data = base58_encode(_h + cc)
        byte[] data = new byte[25];
        System.arraycopy(h, 0, data, 0, 21);
        System.arraycopy(cc,0, data, 21, 4);

        this.string  = Utils.base58Encode(data);
        this.network = network;
        this.code    = userNumber(cc);
        this.valid   = true;
    }

    public String toString() {
        return this.string;
    }

    public boolean equals(Address address) {
        return equals(address.toString());
    }

    public boolean equals(String address) {
        return valid && string.equals(address);
    }

    private static byte[] checkCode(byte[] data) {
        byte[] sha256d = Utils.sha256(Utils.sha256(data));
        byte[] cc = new byte[4];
        System.arraycopy(sha256d, 0, cc, 0, 4);
        return cc;
    }

    private static long userNumber(byte[] cc) {
        return (long)(cc[3] & 0xFF) << 24 | (cc[2] & 0xFF) << 16 | (cc[1] & 0xFF) << 8 | (cc[0] & 0xFF);
    }

    public static void main(String args[]) {
        {
            Address address = new Address("4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk");
            System.out.println("address: " + address);
            System.out.println("number: " + address.code);
        }
        {
            Address address = new Address("4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
            System.out.println("address: " + address);
            System.out.println("number: " + address.code);
        }
    }
}
