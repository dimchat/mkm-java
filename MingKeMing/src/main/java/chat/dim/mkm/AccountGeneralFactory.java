/* license: https://mit-license.org
 *
 *  Ming-Ke-Ming : Decentralized User Identity Authentication
 *
 *                                Written in 2022 by Moky <albert.moky@gmail.com>
 *
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Albert Moky
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;
import chat.dim.format.TransportableData;
import chat.dim.protocol.Address;
import chat.dim.protocol.Document;
import chat.dim.protocol.ID;
import chat.dim.protocol.Meta;
import chat.dim.type.Converter;
import chat.dim.type.Wrapper;

/**
 *  Account GeneralFactory
 *  ~~~~~~~~~~~~~~~~~~~~~~
 */
public class AccountGeneralFactory {

    private Address.Factory addressFactory = null;

    private ID.Factory idFactory = null;

    private final Map<Integer, Meta.Factory> metaFactories = new HashMap<>();

    private final Map<String, Document.Factory> documentFactories = new HashMap<>();

    //
    //  Address
    //

    public void setAddressFactory(Address.Factory factory) {
        addressFactory = factory;
    }

    public Address.Factory getAddressFactory() {
        return addressFactory;
    }

    public Address parseAddress(Object address) {
        if (address == null) {
            return null;
        } else if (address instanceof Address) {
            return (Address) address;
        }
        String str = Wrapper.getString(address);
        assert str != null : "address error: " + address;
        Address.Factory factory = getAddressFactory();
        assert factory != null : "address factory not ready";
        return factory.parseAddress(str);
    }

    public Address createAddress(String address) {
        Address.Factory factory = getAddressFactory();
        assert factory != null : "address factory not ready";
        return factory.createAddress(address);
    }

    public Address generateAddress(Meta meta, int network) {
        Address.Factory factory = getAddressFactory();
        assert factory != null : "address factory not ready";
        return factory.generateAddress(meta, network);
    }

    //
    //  ID
    //

    public void setIdentifierFactory(ID.Factory factory) {
        idFactory = factory;
    }

    public ID.Factory getIdentifierFactory() {
        return idFactory;
    }

    public ID parseIdentifier(Object identifier) {
        if (identifier == null) {
            return null;
        } else if (identifier instanceof ID) {
            return (ID) identifier;
        }
        String str = Wrapper.getString(identifier);
        assert str != null : "ID error: " + identifier;
        ID.Factory factory = getIdentifierFactory();
        assert factory != null : "ID factory not ready";
        return factory.parseIdentifier(str);
    }

    public ID createIdentifier(String name, Address address, String terminal) {
        ID.Factory factory = getIdentifierFactory();
        assert factory != null : "ID factory not ready";
        return factory.createIdentifier(name, address, terminal);
    }

    public ID generateIdentifier(Meta meta, int network, String terminal) {
        ID.Factory factory = getIdentifierFactory();
        assert factory != null : "ID factory not ready";
        return factory.generateIdentifier(meta, network, terminal);
    }

    public List<ID> convertIdentifiers(List<?> members) {
        List<ID> array = new ArrayList<>();
        ID id;
        for (Object item : members) {
            id = parseIdentifier(item);
            if (id == null) {
                continue;
            }
            array.add(id);
        }
        return array;
    }
    public List<String> revertIdentifiers(List<ID> members) {
        List<String> array = new ArrayList<>();
        for (ID item : members) {
            array.add(item.toString());
        }
        return array;
    }

    //
    //  Meta
    //

    public void setMetaFactory(int version, Meta.Factory factory) {
        metaFactories.put(version, factory);
    }

    public Meta.Factory getMetaFactory(int version) {
        return metaFactories.get(version);
    }

    public int getMetaType(Map<?, ?> meta, int defaultValue) {
        return Converter.getInt(meta.get("type"), defaultValue);
    }

    public Meta createMeta(int version, VerifyKey key, String seed, TransportableData fingerprint) {
        Meta.Factory factory = getMetaFactory(version);
        assert factory != null : "meta type not found: " + version;
        return factory.createMeta(key, seed, fingerprint);
    }

    public Meta generateMeta(int version, SignKey sKey, String seed) {
        Meta.Factory factory = getMetaFactory(version);
        assert factory != null : "meta type not found: " + version;
        return factory.generateMeta(sKey, seed);
    }
    public Meta parseMeta(Object meta) {
        if (meta == null) {
            return null;
        } else if (meta instanceof Meta) {
            return (Meta) meta;
        }
        Map<String, Object> info = Wrapper.getMap(meta);
        if (info == null) {
            assert false : "meta error: " + meta;
            return null;
        }
        int version = getMetaType(info, 0);
        assert version > 0 : "meta type error: " + meta;
        Meta.Factory factory = getMetaFactory(version);
        if (factory == null) {
            factory = getMetaFactory(0);  // unknown
            assert factory != null : "default meta factory not found";
        }
        return factory.parseMeta(info);
    }

    //
    //  Document
    //

    public void setDocumentFactory(String type, Document.Factory factory) {
        documentFactories.put(type, factory);
    }

    public Document.Factory getDocumentFactory(String type) {
        return documentFactories.get(type);
    }

    public String getDocumentType(Map<?, ?> doc, String defaultValue) {
        return Converter.getString(doc.get("type"), defaultValue);
    }

    public Document createDocument(String type, ID identifier, String data, TransportableData signature) {
        Document.Factory factory = getDocumentFactory(type);
        assert factory != null : "document type not found: " + type;
        return factory.createDocument(identifier, data, signature);
    }

    public Document parseDocument(Object doc) {
        if (doc == null) {
            return null;
        } else if (doc instanceof Document) {
            return (Document) doc;
        }
        Map<String, Object> info = Wrapper.getMap(doc);
        if (info == null) {
            assert false : "document error: " + doc;
            return null;
        }
        String type = getDocumentType(info, "*");
        Document.Factory factory = getDocumentFactory(type);
        if (factory == null) {
            assert !type.equals("*") : "document factory not ready: " + doc;
            factory = getDocumentFactory("*");  // unknown
            assert factory != null : "default document factory not found";
        }
        return factory.parseDocument(info);
    }
}
