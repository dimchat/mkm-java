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

import chat.dim.type.Stringer;
import chat.dim.type.Wrapper;

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
public interface ID extends Stringer {

    String getName();
    Address getAddress();
    String getTerminal();

    /**
     *  Get ID.type
     *
     * @return network type
     */
    int getType();

    // ID types
    boolean isBroadcast();
    boolean isUser();
    boolean isGroup();

    /**
     *  ID for Broadcast
     */
    ID ANYONE = create("anyone", Address.ANYWHERE, null);
    ID EVERYONE = create("everyone", Address.EVERYWHERE, null);

    /**
     *  DIM Founder
     */
    ID FOUNDER = create("moky", Address.ANYWHERE, null);

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
    static ID parse(Object identifier) {
        if (identifier == null) {
            return null;
        } else if (identifier instanceof ID) {
            return (ID) identifier;
        }
        String str = Wrapper.getString(identifier);
        assert str != null : "ID error: " + identifier;
        Factory factory = getFactory();
        assert factory != null : "ID factory not ready";
        return factory.parseID(str);
    }

    static ID create(String name, Address address, String terminal) {
        Factory factory = getFactory();
        assert factory != null : "ID factory not ready";
        return factory.createID(name, address, terminal);
    }

    static ID generate(Meta meta, int type, String terminal) {
        Factory factory = getFactory();
        assert factory != null : "ID factory not ready";
        return factory.generateID(meta, type, terminal);
    }

    static Factory getFactory() {
        return AccountFactories.idFactory;
    }
    static void setFactory(Factory factory) {
        AccountFactories.idFactory = factory;
    }

    /**
     *  ID Factory
     *  ~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Generate ID
         *
         * @param meta     - meta info
         * @param type     - ID.type
         * @param terminal - ID.terminal
         * @return ID
         */
        ID generateID(Meta meta, int type, String terminal);

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
