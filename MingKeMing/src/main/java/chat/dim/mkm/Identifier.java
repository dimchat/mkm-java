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

import chat.dim.protocol.Address;
import chat.dim.protocol.ID;

/**
 *  ID for entity (User/Group)
 *
 *      data format: "name@address[/terminal]"
 *
 *      fields:
 *          name     - entity name, the seed of fingerprint to build address
 *          address  - a string to identify an entity
 *          terminal - entity login resource(device), OPTIONAL
 */
final class Identifier extends chat.dim.type.String implements ID {

    private final String name;
    private final Address address;
    private final String terminal;

    Identifier(String identifier, String name, Address address, String terminal) {
        super(identifier);
        this.name = name;
        this.address = address;
        this.terminal = terminal;
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            // same object
            return true;
        }
        if (other instanceof ID) {
            // compare with name & address
            return ID.equals(this, (ID) other);
        }
        String str;
        if (other instanceof chat.dim.type.String) {
            str = other.toString();
        } else if (other instanceof String) {
            str = (String) other;
        } else {
            // null or unknown object
            return false;
        }
        // comparing without terminal
        String[] pair = str.split("/");
        assert pair[0].length() > 0 : "ID error: " + other;
        if (terminal == null || terminal.length() == 0) {
            return pair[0].equals(toString());
        } else {
            return pair[0].equals(toString().split("/")[0]);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public String getTerminal() {
        return terminal;
    }

    /**
     *  Get Network ID
     *
     * @return address type as network ID
     */
    @Override
    public byte getType() {
        assert address != null : "ID.address should not be empty: " + toString();
        return address.getNetwork();
    }
}
