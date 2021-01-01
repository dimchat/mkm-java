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

import chat.dim.mkm.BroadcastAddress;
import chat.dim.mkm.Factories;

/**
 *  Address for MKM ID
 *  ~~~~~~~~~~~~~~~~~~
 *  This class is used to build address for ID
 */
public interface Address {

    /**
     *  Get address type
     *
     * @return network type
     */
    byte getNetwork();

    static boolean isBroadcast(Address address) {
        return address instanceof BroadcastAddress;
    }
    static boolean isUser(Address address) {
        return NetworkType.isUser(address.getNetwork());
    }
    static boolean isGroup(Address address) {
        return NetworkType.isGroup(address.getNetwork());
    }

    /**
     *  Address for broadcast
     */
    BroadcastAddress ANYWHERE = new BroadcastAddress("anywhere", NetworkType.MAIN);
    BroadcastAddress EVERYWHERE = new BroadcastAddress("everywhere", NetworkType.MAIN);

    //
    //  Factory method
    //
    static Address parse(Object address) {
        if (address == null) {
            return null;
        } else if (address instanceof Address) {
            return (Address) address;
        }
        String string;
        if (address instanceof String) {
            string = (String) address;
        } else if (address instanceof chat.dim.type.String) {
            string = address.toString();
        } else {
            throw new IllegalArgumentException("Illegal address: " + address);
        }
        Factory factory = getFactory();
        if (factory == null) {
            throw new NullPointerException("address factory not found");
        }
        return factory.parseAddress(string);
    }

    static Factory getFactory() {
        return Factories.addressFactory;
    }
    static void setFactory(Factory factory) {
        Factories.addressFactory = factory;
    }

    /**
     *  Address Factory
     *  ~~~~~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Parse string object to address
         *
         * @param address - address string
         * @return Address
         */
        Address parseAddress(String address);
    }
}
