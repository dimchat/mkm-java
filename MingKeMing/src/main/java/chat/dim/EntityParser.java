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
package chat.dim;

import java.util.HashMap;
import java.util.Map;

import chat.dim.mkm.BaseBulletin;
import chat.dim.mkm.BaseDocument;
import chat.dim.mkm.BaseVisa;
import chat.dim.mkm.Identifier;
import chat.dim.protocol.Address;
import chat.dim.protocol.Document;
import chat.dim.protocol.ID;
import chat.dim.protocol.Meta;
import chat.dim.protocol.NetworkType;

public abstract class EntityParser implements Entity.Parser {

    protected final Map<String, ID> idMap = new HashMap<>();
    protected final Map<String, Address> addressMap = new HashMap<>();

    private ID createID(String string) {
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
            address = parseAddress(pair[0]);
        } else {
            // got name & address
            assert pair.length == 2 : "ID error: " + string;
            assert pair[1].length() > 0 : "ID.address error: " + string;
            name = pair[0];
            address = parseAddress(pair[1]);
        }
        if (address == null) {
            return null;
        }
        return new Identifier(name, address, terminal);
    }

    @Override
    public ID parseID(Object identifier) {
        if (identifier == null) {
            return null;
        } else if (identifier instanceof ID) {
            return (ID) identifier;
        }
        assert identifier instanceof String : "ID error: " + identifier;
        String string = (String) identifier;
        // get from cache
        ID id = idMap.get(string);
        if (id == null) {
            id = createID(string);
            if (id != null) {
                idMap.put(string, id);
            }
        }
        return id;
    }

    /**
     *  Create address from string
     *
     * @param string - address string
     * @return Address
     */
    protected Address createAddress(String string) {
        // Address for broadcast
        if (Address.ANYWHERE.equalsIgnoreCase(string)) {
            return Address.ANYWHERE;
        }
        if (Address.EVERYWHERE.equalsIgnoreCase(string)) {
            return Address.EVERYWHERE;
        }
        // implements by sub class
        return null;
    }

    @Override
    public Address parseAddress(Object address) {
        if (address == null) {
            return null;
        } else if (address instanceof Address) {
            return (Address) address;
        }
        assert address instanceof String : "address error: " + address;
        String string = (String) address;
        // get from cache
        Address add = addressMap.get(string);
        if (add == null) {
            add = createAddress(string);
            if (add != null) {
                addressMap.put(string, add);
            }
        }
        return add;
    }

    /**
     *  Create meta from map info
     *
     * @param meta - meta info
     * @return Meta
     */
    protected abstract Meta createMeta(Map<String, Object> meta);

    @SuppressWarnings("unchecked")
    @Override
    public Meta parseMeta(Map<String, Object> meta) {
        if (meta == null) {
            return null;
        } else if (meta instanceof Meta) {
            return (Meta) meta;
        }
        return createMeta(meta);
    }

    /**
     *  Create document from map info
     *
     * @param doc - document info
     * @return Document
     */
    protected Document createDocument(Map<String, Object> doc) {
        ID identifier = parseID(doc.get("ID"));
        if (identifier == null) {
            return null;
        }
        if (NetworkType.isUser(identifier.getType())) {
            String type = (String) doc.get("type");
            if (Document.VISA.equals(type)) {
                return new BaseVisa(doc);
            }
        } else if (NetworkType.isGroup(identifier.getType())) {
            return new BaseBulletin(doc);
        }
        return new BaseDocument(doc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Document parseDocument(Map<String, Object> doc) {
        if (doc == null) {
            return null;
        } else if (doc instanceof Document) {
            return (Document) doc;
        }
        return createDocument(doc);
    }
}
