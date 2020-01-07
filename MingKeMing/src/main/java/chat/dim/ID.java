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
package chat.dim;

import chat.dim.format.JSON;
import chat.dim.protocol.NetworkType;

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
public final class ID extends chat.dim.type.String {

    public final String name;
    public final Address address;
    public final String terminal;

    private ID(String string) {
        super(string);
        // split ID string
        String[] pair = string.split("/");
        // terminal
        if (pair.length == 1) {
            // no terminal
            this.terminal = null;
        } else {
            // got terminal
            assert pair.length == 2 : "ID error: " + string;
            assert pair[1].length() > 0 : "ID.terminal error: " + string;
            this.terminal = pair[1];
        }
        // name @ address
        assert pair[0].length() > 0 : "ID error: " + string;
        pair = pair[0].split("@");
        assert pair[0].length() > 0 : "ID error: " + string;
        if (pair.length == 1) {
            // got address without name
            this.name = null;
            this.address = Address.getInstance(pair[0]);
        } else {
            // got name & address
            assert pair.length == 2 : "ID error: " + string;
            assert pair[1].length() > 0 : "ID.address error: " + string;
            this.name = pair[0];
            this.address = Address.getInstance(pair[1]);
        }
    }

    public ID(String name, Address address) {
        this(name, address, null);
    }

    public ID(String name, Address address, String terminal) {
        super(concat(name, address, terminal));
        this.name = name;
        this.address = address;
        this.terminal = terminal;
    }

    private static String concat(String name, Address address, String terminal) {
        String string = address.toString();
        if (name != null && name.length() > 0) {
            string = name + "@" + string;
        }
        if (terminal != null && terminal.length() > 0) {
            string = string + "/" + terminal;
        }
        return string;
    }

    /**
     *  Get Network ID
     *
     * @return address type as network ID
     */
    public NetworkType getType() {
        assert address != null : "ID.address should not be empty: " + string;
        return address.getNetwork();
    }

    /**
     *  Get Search Number
     *
     * @return number for searching this ID
     */
    public long getNumber() {
        assert address != null : "ID.address should not be empty: " + string;
        return address.getCode();
    }

    /**
     *  Get whether ID string is valid
     *
     * @return False on address error
     */
    public boolean isValid() {
        return getNumber() > 0;
    }

    @SuppressWarnings("Contract")
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
            if (!address.equals(identifier.address)) {
                return false;
            }
            // check name
            if (name == null || name.length() == 0) {
                return identifier.name == null || identifier.name.length() == 0;
            } else {
                return name.equals(identifier.name);
            }
        }
        assert other instanceof String : "ID error: " + other;
        // comparing without terminal
        String[] pair = ((String) other).split("/");
        assert pair[0].length() > 0 : "ID error: " + other;
        if (terminal == null || terminal.length() == 0) {
            return pair[0].equals(string);
        } else {
            return pair[0].equals(string.split("/")[0]);
        }
    }

    //-------- Runtime --------

    /**
     *  Create/get instance of ID
     *
     * @param object - ID string/object
     * @return ID object
     */
    public static ID getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof ID) {
            return (ID) object;
        }
        assert object instanceof String : "ID must be a string: " + object;
        String string = (String) object;
        return new ID(string);
    }

    /**
     *  ID for broadcast
     */
    public static final ID ANYONE = new ID("anyone", Address.ANYWHERE, null);
    public static final ID EVERYONE = new ID("everyone", Address.EVERYWHERE, null);

    public boolean isBroadcast() {
        assert address != null : "ID.address should not be empty: " + string;
        return address.isBroadcast();
    }

    static {
        //
        //  Register class for JsON
        //

        JSON.registerStringClass(ID.class);
    }
}
