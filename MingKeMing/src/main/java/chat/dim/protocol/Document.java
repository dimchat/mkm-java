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

import java.util.Map;

import chat.dim.mkm.Factories;
import chat.dim.type.SOMap;

/**
 *  User/Group Profile
 *  ~~~~~~~~~~~~~~~~~~
 *  This class is used to generate entity profile
 *
 *      data format: {
 *          ID: "EntityID",   // entity ID
 *          data: "{JSON}",   // data = json_encode(info)
 *          signature: "..."  // signature = sign(data, SK);
 *      }
 */
public interface Document extends TAI, SOMap {

    //
    //  Document types
    //
    String VISA     = "visa";      // for login/communication
    String PROFILE  = "profile";   // for user info
    String BULLETIN = "bulletin";  // for group info

    /**
     *  Get document type
     *
     * @return document type
     */
    String getType();

    static String getType(Map<String, Object> doc) {
        return (String) doc.get("type");
    }

    /**
     *  Get entity ID
     *
     * @return entity ID
     */
    ID getIdentifier();

    static ID getIdentifier(Map<String, Object> doc) {
        return ID.parse(doc.get("ID"));
    }

    //---- properties getter/setter

    /**
     *  Get entity name
     *
     * @return name string
     */
    String getName();
    void setName(String value);

    //
    //  Factory methods
    //
    static Document create(ID identifier, String type, byte[] data, byte[] signature) {
        Factory factory = getFactory(type);
        if (factory == null) {
            throw new NullPointerException("document type not found: " + type);
        }
        return factory.createDocument(identifier, data, signature);
    }
    static Document create(ID identifier, String type) {
        Factory factory = getFactory(type);
        if (factory == null) {
            throw new NullPointerException("document type not found: " + type);
        }
        return factory.createDocument(identifier);
    }
    static Document parse(Map<String, Object> doc) {
        if (doc == null) {
            return null;
        } else if (doc instanceof Document) {
            return (Document) doc;
        } else if (doc instanceof SOMap) {
            doc = ((SOMap) doc).getMap();
        }
        String type = getType(doc);
        Factory factory = getFactory(type);
        if (factory == null) {
            factory = getFactory("*");  // unknown
            if (factory == null) {
                throw new NullPointerException("cannot parse document: " + doc);
            }
        }
        return factory.parseDocument(doc);
    }

    static Factory getFactory(String type) {
        return Factories.documentFactories.get(type);
    }
    static void register(String type, Factory factory) {
        Factories.documentFactories.put(type, factory);
    }

    /**
     *  Document Factory
     *  ~~~~~~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Create document with data & signature loaded from local storage
         *
         * @param identifier - entity ID
         * @param data       - document data
         * @param signature  - document signature
         * @return Document
         */
        Document createDocument(ID identifier, byte[] data, byte[] signature);

        /**
         *  Create empty document with entity ID & document type
         *
         * @param identifier - entity ID
         * @return Document
         */
        Document createDocument(ID identifier);

        /**
         *  Parse map object to entity document
         *
         * @param doc - info
         * @return Document
         */
        Document parseDocument(Map<String, Object> doc);
    }
}
