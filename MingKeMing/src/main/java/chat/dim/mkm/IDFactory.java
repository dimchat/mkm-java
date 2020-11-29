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

import java.util.HashMap;
import java.util.Map;

import chat.dim.protocol.Address;
import chat.dim.protocol.ID;

public final class IDFactory implements ID.Factory {

    private final Map<String, ID> identifiers = new HashMap<>();

    private ID createID(final String string) {
        String name;
        Address address;
        String terminal;
        // split ID string
        String[] pair = string.split("/");
        // terminal
        if (pair.length == 1) {
            // no terminal
            terminal = null;
        } else {
            // got terminal
            assert pair.length == 2 : "ID error: " + string;
            assert pair[1].length() > 0 : "ID.terminal error: " + string;
            terminal = pair[1];
        }
        // name @ address
        assert pair[0].length() > 0 : "ID error: " + string;
        pair = pair[0].split("@");
        assert pair[0].length() > 0 : "ID error: " + string;
        if (pair.length == 1) {
            // got address without name
            name = null;
            address = Address.parse(pair[0]);
        } else {
            // got name & address
            assert pair.length == 2 : "ID error: " + string;
            assert pair[1].length() > 0 : "ID.address error: " + string;
            name = pair[0];
            address = Address.parse(pair[1]);
        }
        if (address == null) {
            return null;
        }
        return new Identifier(string, name, address, terminal);
    }

    @Override
    public ID createID(String name, Address address, String terminal) {
        assert address != null : "ID.address empty";
        String string = Identifier.concat(name, address, terminal);
        ID id = new Identifier(string, name, address, terminal);
        identifiers.put(id.toString(), id);
        return id;
    }

    @Override
    public ID parseID(String identifier) {
        if (ID.ANYONE.equalsIgnoreCase(identifier)) {
            return ID.ANYONE;
        }
        if (ID.EVERYONE.equalsIgnoreCase(identifier)) {
            return ID.EVERYONE;
        }
        ID id = identifiers.get(identifier);
        if (id == null) {
            id = createID(identifier);
            if (id != null) {
                identifiers.put(identifier, id);
            }
        }
        return id;
    }
}
