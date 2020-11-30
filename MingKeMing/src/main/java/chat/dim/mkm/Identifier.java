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

    static String concat(String name, Address address, String terminal) {
        String string = address.toString();
        if (name != null && name.length() > 0) {
            string = name + "@" + string;
        }
        if (terminal != null && terminal.length() > 0) {
            string = string + "/" + terminal;
        }
        return string;
    }

    Identifier(String identifier, String name, Address address, String terminal) {
        super(identifier);
        this.name = name;
        this.address = address;
        this.terminal = terminal;
    }

    public Identifier(String name, Address address, String terminal) {
        this(concat(name, address, terminal), name, address, terminal);
    }

    public Identifier(String name, Address address) {
        this(concat(name, address, null), name, address, null);
    }

    public Identifier(Address address) {
        this(address.toString(), null, address, null);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            // object empty
            return false;
        }
        if (super.equals(other)) {
            // same object
            return true;
        }
        if (other instanceof ID) {
            ID identifier = (ID) other;
            // check address
            Address otherAddress = identifier.getAddress();
            if (!address.equals(otherAddress)) {
                return false;
            }
            // check name
            String otherName = identifier.getName();
            if (name == null || name.length() == 0) {
                return otherName == null || otherName.length() == 0;
            } else {
                return name.equals(otherName);
            }
        }
        assert other instanceof String : "ID error: " + other;
        // comparing without terminal
        String[] pair = ((String) other).split("/");
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
