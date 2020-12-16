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

import java.util.ArrayList;
import java.util.List;

import chat.dim.mkm.Factories;

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
public interface ID {

    String getName();
    Address getAddress();
    String getTerminal();

    /**
     *  Get ID.type
     *
     * @return network type
     */
    byte getType();

    static boolean isBroadcast(ID identifier) {
        return Address.isBroadcast(identifier.getAddress());
    }
    static boolean isUser(ID identifier) {
        return Address.isUser(identifier.getAddress());
    }
    static boolean isGroup(ID identifier) {
        return Address.isGroup(identifier.getAddress());
    }

    /**
     *  ID for broadcast
     */
    ID ANYONE = create("anyone", Address.ANYWHERE, null);
    ID EVERYONE = create("everyone", Address.EVERYWHERE, null);

    static List<ID> convert(List<String> members) {
        List<ID> array = new ArrayList<>();
        ID id;
        for (String item : members) {
            id = parse(item);
            if (id == null) {
                continue;
            }
            array.add(id);
        }
        return array;
    }
    static List<String> revert(List<ID> members) {
        List<String> array = new ArrayList<>();
        for (ID item : members) {
            array.add(item.toString());
        }
        return array;
    }

    //
    //  Factory methods
    //
    static ID create(String name, Address address, String terminal) {
        return Factories.idFactory.createID(name, address, terminal);
    }
    static ID parse(Object identifier) {
        if (identifier == null) {
            return null;
        } else if (identifier instanceof ID) {
            return (ID) identifier;
        }
        String string;
        if (identifier instanceof String) {
            string = (String) identifier;
        } else if (identifier instanceof chat.dim.type.String) {
            string = identifier.toString();
        } else {
            throw new IllegalArgumentException("Illegal ID: " + identifier);
        }
        Factory factory = getFactory();
        if (factory == null) {
            throw new NullPointerException("ID factory not found");
        }
        return factory.parseID(string);
    }

    static Factory getFactory() {
        return Factories.idFactory;
    }
    static void setFactory(Factory factory) {
        Factories.idFactory = factory;
    }

    /**
     *  ID Factory
     *  ~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Create ID
         *
         * @param name     - ID.name
         * @param address  - ID.address
         * @param terminal - ID.terminal
         * @return ID
         */
        ID createID(String name, Address address, String terminal);

        /**
         *  Parse string object to ID
         *
         * @param identifier - ID string
         * @return ID
         */
        ID parseID(String identifier);
    }
}
