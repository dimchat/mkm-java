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
import chat.dim.plugins.SharedAccountExtensions;
import chat.dim.type.Stringer;

/**
 *  Address for MKM ID
 *  <p>
 *      This class is used to build address for ID
 *  </p>
 */
public interface Address extends Stringer {

    /**
     *  Get address type
     *
     * @return network id
     */
    int getNetwork();

    /**
     *  Address for broadcast
     */
    BroadcastAddress ANYWHERE = new BroadcastAddress("anywhere", EntityType.ANY);
    BroadcastAddress EVERYWHERE = new BroadcastAddress("everywhere", EntityType.EVERY);

    //
    //  Factory methods
    //
    static Address parse(Object address) {
        return SharedAccountExtensions.addressHelper.parseAddress(address);
    }

    static Address generate(Meta meta, int network) {
        return SharedAccountExtensions.addressHelper.generateAddress(meta, network);
    }

    static Factory getFactory() {
        return SharedAccountExtensions.addressHelper.getAddressFactory();
    }
    static void setFactory(Factory factory) {
        SharedAccountExtensions.addressHelper.setAddressFactory(factory);
    }

    /**
     *  Address Factory
     */
    interface Factory {

        /**
         *  Generate address with meta &amp; type
         *
         * @param meta    - meta info
         * @param network - address type
         * @return Address
         */
        Address generateAddress(Meta meta, int network);

        /**
         *  Parse string object to address
         *
         * @param address - address string
         * @return Address
         */
        Address parseAddress(String address);
    }

}
