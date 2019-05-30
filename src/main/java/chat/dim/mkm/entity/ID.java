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

/**
 *  ID for entity (Account/Group)
 *
 *      data format: "name@address[/terminal]"
 *
 *      fileds:
 *          name     - entity name, the seed of fingerprint to build address
 *          address  - a string to identify an entity
 *          terminal - entity login resource(device), OPTIONAL
 */
public final class ID {

    private final String string;

    public final String name;
    public final Address address;
    public final String terminal;

    public ID(String string) {
        this.string = string;
        // terminal
        String[] pair = string.split("/", 2);
        if (pair.length > 1) {
            this.terminal = pair[1];
        } else {
            this.terminal = null;
        }
        // name & address
        pair = pair[0].split("@", 2);
        if (pair.length > 1) {
            this.name = pair[0];
            this.address = new Address(pair[1]);
        } else {
            this.name = null;
            this.address = new Address(pair[0]);
        }
    }

    public ID(String name, Address address) {
        if (name == null || name.length() == 0) {
            // BTC address?
            this.string = address.toString();
        } else {
            this.string = name + "@" + address;
        }
        this.terminal = null;
        this.name = name;
        this.address = address;
    }

    /**
     *  For BTC address
     *
     * @param address - BTC address
     */
    public ID(Address address) {
        this(null, address);
    }

    public static ID getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof ID) {
            return (ID) object;
        } else if (object instanceof String) {
            return new ID((String) object);
        } else {
            throw new IllegalArgumentException("unknown ID:" + object);
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
        }
        // convert to ID object
        ID identifier = ID.getInstance(other);
        if (identifier == null) {
            // null
            return false;
        }
        // check with name & address
        if (name != null && !name.equals(identifier.name)) {
            // ID.name not match
            return false;
        }
        return address.equals(identifier.address);
    }

    @Override
    public String toString() {
        return string;
    }

    /**
     *  Get Network ID
     *
     * @return address type as network ID
     */
    public NetworkType getType() {
        return address.network;
    }

    /**
     *  Get Search Number
     *
     * @return number for searching this ID
     */
    public long getNumber() {
        return address.code;
    }

    public boolean isValid() {
        return address.valid;
    }
}
