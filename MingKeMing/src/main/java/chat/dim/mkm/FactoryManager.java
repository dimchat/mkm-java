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
import chat.dim.format.UTF8;
import chat.dim.protocol.Address;
import chat.dim.protocol.Document;
import chat.dim.protocol.ID;
import chat.dim.protocol.Meta;
import chat.dim.protocol.MetaType;
import chat.dim.type.Wrapper;

public enum FactoryManager {

    INSTANCE;

    public static FactoryManager getInstance() {
        return INSTANCE;
    }

    public GeneralFactory generalFactory = new GeneralFactory();

    public static class GeneralFactory {

        public Address.Factory addressFactory = null;

        public ID.Factory idFactory = null;

        public final Map<Integer, Meta.Factory> metaFactories = new HashMap<>();

        public final Map<String, Document.Factory> documentFactories = new HashMap<>();

        //
        //  Address
        //

        public Address parseAddress(Object address) {
            if (address == null) {
                return null;
            } else if (address instanceof Address) {
                return (Address) address;
            }
            String str = Wrapper.getString(address);
            assert str != null : "address error: " + address;
            Address.Factory factory = addressFactory;
            assert factory != null : "address factory not ready";
            return factory.parseAddress(str);
        }

        public Address createAddress(String address) {
            Address.Factory factory = addressFactory;
            assert factory != null : "address factory not ready";
            return factory.createAddress(address);
        }

        public Address generateAddress(Meta meta, int network) {
            Address.Factory factory = addressFactory;
            assert factory != null : "address factory not ready";
            return factory.generateAddress(meta, network);
        }

        //
        //  ID
        //

        public ID parseID(Object identifier) {
            if (identifier == null) {
                return null;
            } else if (identifier instanceof ID) {
                return (ID) identifier;
            }
            String str = Wrapper.getString(identifier);
            assert str != null : "ID error: " + identifier;
            ID.Factory factory = idFactory;
            assert factory != null : "ID factory not ready";
            return factory.parseID(str);
        }

        public ID createID(String name, Address address, String terminal) {
            ID.Factory factory = idFactory;
            assert factory != null : "ID factory not ready";
            return factory.createID(name, address, terminal);
        }

        public ID generateID(Meta meta, int network, String terminal) {
            ID.Factory factory = idFactory;
            assert factory != null : "ID factory not ready";
            return factory.generateID(meta, network, terminal);
        }

        public List<ID> convertIdentifiers(List<?> members) {
            List<ID> array = new ArrayList<>();
            ID id;
            for (Object item : members) {
                id = parseID(item);
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

        int getMetaType(Map<String, Object> meta) {
            Object version = meta.get("type");
            return ((Number) version).intValue();
        }

        public Meta createMeta(int version, VerifyKey key, String seed, byte[] fingerprint) {
            Meta.Factory factory = metaFactories.get(version);
            assert factory != null : "meta type not found: " + version;
            return factory.createMeta(key, seed, fingerprint);
        }

        public Meta generateMeta(int version, SignKey sKey, String seed) {
            Meta.Factory factory = metaFactories.get(version);
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
            assert info != null : "meta error: " + meta;
            int version = getMetaType(info);
            Meta.Factory factory = metaFactories.get(version);
            if (factory == null) {
                factory = metaFactories.get(0);  // unknown
                assert factory != null : "cannot parse entity meta: " + meta;
            }
            return factory.parseMeta(info);
        }

        public boolean checkMeta(Meta meta) {
            VerifyKey key = meta.getKey();
            if (key == null) {
                // meta.key should not be empty
                return false;
            }
            if (!MetaType.hasSeed(meta.getType())) {
                // this meta has no seed, so no signature too
                return true;
            }
            // check seed with signature
            String seed = meta.getSeed();
            byte[] fingerprint = meta.getFingerprint();
            if (seed == null || fingerprint == null) {
                // seed and fingerprint should not be empty
                return false;
            }
            // verify fingerprint
            return key.verify(UTF8.encode(seed), fingerprint);
        }
        public boolean matches(ID identifier, Meta meta) {
            // check ID.name
            String seed = meta.getSeed();
            String name = identifier.getName();
            if (name == null || name.length() == 0) {
                if (seed != null && seed.length() > 0) {
                    return false;
                }
            } else if (!name.equals(seed)) {
                return false;
            }
            // check ID.address
            Address old = identifier.getAddress();
            //assert old != null : "ID error: " + identifier;
            Address gen = Address.generate(meta, old.getType());
            return old.equals(gen);
        }
        public boolean matches(VerifyKey pk, Meta meta) {
            // check whether the public key equals to meta.key
            if (pk.equals(meta.getKey())) {
                return true;
            }
            // check with seed & fingerprint
            if (MetaType.hasSeed(meta.getType())) {
                // check whether keys equal by verifying signature
                String seed = meta.getSeed();
                byte[] fingerprint = meta.getFingerprint();
                return pk.verify(UTF8.encode(seed), fingerprint);
            } else {
                // NOTICE: ID with BTC/ETH address has no username, so
                //         just compare the key.data to check matching
                return false;
            }
        }

        //
        //  Document
        //

        String getDocumentType(Map<String, Object> doc) {
            return (String) doc.get("type");
        }

        public Document createDocument(String type, ID identifier, String data, String signature) {
            Document.Factory factory = documentFactories.get(type);
            assert factory != null : "document type not found: " + type;
            return factory.createDocument(identifier, data, signature);
        }
        public Document createDocument(String type, ID identifier) {
            Document.Factory factory = documentFactories.get(type);
            assert factory != null : "document type not found: " + type;
            return factory.createDocument(identifier);
        }
        public Document parseDocument(Object doc) {
            if (doc == null) {
                return null;
            } else if (doc instanceof Document) {
                return (Document) doc;
            }
            Map<String, Object> info = Wrapper.getMap(doc);
            assert info != null : "document error: " + doc;
            String type = getDocumentType(info);
            Document.Factory factory = documentFactories.get(type);
            if (factory == null) {
                factory = documentFactories.get("*");  // unknown
                assert factory != null : "cannot parse entity document: " + doc;
            }
            return factory.parseDocument(info);
        }
    }
}
