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

import chat.dim.mkm.BroadcastID;
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

    static boolean isUser(ID identifier) {
        return NetworkType.isUser(identifier.getType());
    }
    static boolean isGroup(ID identifier) {
        return NetworkType.isGroup(identifier.getType());
    }

    /**
     *  ID for broadcast
     */
    BroadcastID ANYONE = new BroadcastID("anyone", Address.ANYWHERE);
    BroadcastID EVERYONE = new BroadcastID("everyone", Address.EVERYWHERE);

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
        } else if (identifier instanceof String) {
            return Factories.idFactory.parseID((String) identifier);
        } else if (identifier instanceof chat.dim.type.String) {
            chat.dim.type.String string = (chat.dim.type.String) identifier;
            return Factories.idFactory.parseID(string.toString());
        } else {
            throw new IllegalArgumentException("Illegal ID: " + identifier);
        }
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
