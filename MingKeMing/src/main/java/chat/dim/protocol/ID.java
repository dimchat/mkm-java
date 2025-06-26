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

import chat.dim.mkm.Identifier;
import chat.dim.plugins.SharedAccountExtensions;
import chat.dim.type.Stringer;

/**
 *  ID for entity (User/Group)
 *  <p>
 *      data format: "name@address[/terminal]"
 *  </p>
 *
 *  <pre>
 *  fields:
 *      name     - entity name, the seed of fingerprint (for building address)
 *      address  - a string to identify an entity
 *      terminal - location (device), RESERVED
 *  </pre>
 */
public interface ID extends Stringer {

    String getName();
    Address getAddress();
    String getTerminal();

    /**
     *  Get ID type
     *
     * @return network id of address
     */
    int getType();

    // ID types
    boolean isBroadcast();
    boolean isUser();
    boolean isGroup();

    /**
     *  ID for Broadcast
     */
    ID ANYONE = Identifier.create("anyone", Address.ANYWHERE, null);
    ID EVERYONE = Identifier.create("everyone", Address.EVERYWHERE, null);
    // DIM Founder
    ID FOUNDER = Identifier.create("moky", Address.ANYWHERE, null);

    //
    //  Conveniences
    //

    /**
     *  Convert ID list from string array
     *
     * @param members - string array
     * @return ID list
     */
    static List<ID> convert(Iterable<?> members) {
        List<ID> array = new ArrayList<>();
        ID did;
        for (Object item : members) {
            did = parse(item);
            if (did == null) {
                continue;
            }
            array.add(did);
        }
        return array;
    }

    /**
     *  Revert ID list to string array
     *
     * @param members - ID list
     * @return string array
     */
    static List<String> revert(Iterable<ID> members) {
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
        return SharedAccountExtensions.idHelper.parseIdentifier(identifier);
    }
    static ID create(String name, Address address, String terminal) {
        return SharedAccountExtensions.idHelper.createIdentifier(name, address, terminal);
    }
    static ID generate(Meta meta, int network, String terminal) {
        return SharedAccountExtensions.idHelper.generateIdentifier(meta, network, terminal);
    }

    static Factory getFactory() {
        return SharedAccountExtensions.idHelper.getIdentifierFactory();
    }
    static void setFactory(Factory factory) {
        SharedAccountExtensions.idHelper.setIdentifierFactory(factory);
    }

    /**
     *  General Helper
     */
    interface Helper {

        void setIdentifierFactory(Factory factory);
        Factory getIdentifierFactory();

        ID parseIdentifier(Object identifier);

        ID createIdentifier(String name, Address address, String terminal);

        ID generateIdentifier(Meta meta, int network, String terminal);

    }

    /**
     *  ID Factory
     */
    interface Factory {

        /**
         *  Generate ID
         *
         * @param meta     - meta info
         * @param network  - ID.type
         * @param terminal - ID.terminal
         * @return ID
         */
        ID generateIdentifier(Meta meta, int network, String terminal);

        /**
         *  Create ID
         *
         * @param name     - ID.name
         * @param address  - ID.address
         * @param terminal - ID.terminal
         * @return ID
         */
        ID createIdentifier(String name, Address address, String terminal);

        /**
         *  Parse string object to ID
         *
         * @param identifier - ID string
         * @return ID
         */
        ID parseIdentifier(String identifier);
    }

}
