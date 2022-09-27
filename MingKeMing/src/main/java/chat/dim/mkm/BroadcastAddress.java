/* license: https://mit-license.org
 *
 *  Ming-Ke-Ming : Decentralized User Identity Authentication
 *
 *                                Written in 2020 by Moky <albert.moky@gmail.com>
 *
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Albert Moky
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

import chat.dim.protocol.Address;
import chat.dim.protocol.EntityType;
import chat.dim.type.ConstantString;

public final class BroadcastAddress extends ConstantString implements Address {

    private final byte network;

    /**
     *  Create address
     *
     * @param string - Encoded address string
     * @param network - address type
     */
    public BroadcastAddress(String string, EntityType network) {
        super(string);
        this.network = network.value;
    }

    /**
     *  get address type
     *
     * @return Network ID
     */
    @Override
    public byte getType() {
        return network;
    }

    @Override
    public boolean isBroadcast() {
        return true;
    }

    @Override
    public boolean isUser() {
        return network == EntityType.ANY.value;
    }

    @Override
    public boolean isGroup() {
        return network == EntityType.EVERY.value;
    }
}
