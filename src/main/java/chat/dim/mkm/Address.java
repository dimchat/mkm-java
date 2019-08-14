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
package chat.dim.mkm;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import chat.dim.mkm.plugins.BTCAddress;

/**
 *  Address for MKM ID
 *  ~~~~~~~~~~~~~~~~~~
 *  This class is used to build address for ID
 *
 *      properties:
 *          network - address type
 *          number  - search number
 */
public abstract class Address {

    private final String string;

    /**
     *  Called by 'getInstance()' to create address
     *
     *  @param string - Encoded address string
     */
    protected Address(String string) {
        this.string  = string;
    }

    /**
     *  get address type
     *
     * @return Network ID
     */
    public abstract NetworkType getNetwork();

    /**
     *  get search number
     *
     * @return check code
     */
    public abstract long getCode();

    @Override
    public String toString() {
        return string;
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

    //-------- Runtime --------

    private static List<Class> addressClasses = new ArrayList<>();

    /**
     *  Add extended Address class to process new format
     *
     * @param clazz - extended Address class
     */
    @SuppressWarnings("unchecked")
    public static void register(Class clazz) {
        // check whether clazz is subclass of Address
        clazz = clazz.asSubclass(Address.class);
        if (!addressClasses.contains(clazz)) {
            addressClasses.add(0, clazz);
        }
    }

    /**
     *  Create/get instance of Address
     *
     * @param object - address string/object
     * @return Address object
     */
    @SuppressWarnings("unchecked")
    public static Address getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Address) {
            return (Address) object;
        }
        assert object instanceof String;
        String string = (String) object;
        // Constant Address
        String lowercase = string.toLowerCase();
        if (lowercase.equals("anywhere")) {
            return ANYWHERE;
        } else if (lowercase.equals("everywhere")) {
            return EVERYWHERE;
        }

        // try each subclass to parse address
        Constructor constructor;
        for (Class clazz: addressClasses) {
            try {
                constructor = clazz.getConstructor(String.class);
                return (Address) constructor.newInstance(string);
            } catch (Exception e) {
                // address format error, try next
                //e.printStackTrace();
            }
        }
        throw new ArithmeticException("unknown address: " + object);
    }

    /**
     *  Address for broadcast
     */
    public static final Address ANYWHERE = new Address("anywhere") {

        @Override
        public NetworkType getNetwork() {
            return NetworkType.Main;
        }

        @Override
        public long getCode() {
            return 9527;
        }
    };
    public static final Address EVERYWHERE = new Address("everywhere") {

        @Override
        public NetworkType getNetwork() {
            return NetworkType.Group;
        }

        @Override
        public long getCode() {
            return 9527;
        }
    };

    public boolean isBroadcast() {
        NetworkType network = getNetwork();
        if (network.isGroup()) {
            return equals(EVERYWHERE);
        }
        if (network.isPerson()) {
            return equals(ANYWHERE);
        }
        return false;
    }

    static {
        // default (BTC)
        register(BTCAddress.class);
        // ETH
        // ...
    }
}
