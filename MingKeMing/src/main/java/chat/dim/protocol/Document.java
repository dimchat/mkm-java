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
import java.util.Date;
import java.util.List;
import java.util.Map;

import chat.dim.format.TransportableData;
import chat.dim.plugins.SharedAccountExtensions;
import chat.dim.type.Mapper;

/**
 *  User/Group Profile
 *  <p>
 *      This class is used to generate entity profile
 *  </p>
 *
 *  <blockquote><pre>
 *  data format: {
 *      did       : "EntityID",        // entity ID
 *      data      : "{JSON}",          // data = json_encode(info)
 *      signature : "{BASE64_ENCODE}"  // signature = sign(data, SK);
 *  }
 *  </pre></blockquote>
 */
public interface Document extends TAI, Mapper {

    /**
     *  Get entity ID
     *
     * @return entity ID
     */
    ID getIdentifier();

    //---- properties getter/setter

    /**
     *  Get sign time
     *
     * @return date object or null
     */
    Date getTime();

    /**
     *  Get entity name
     *
     * @return name string
     */
    String getName();
    void setName(String value);

    //
    //  Conveniences
    //

    static List<Document> convert(Iterable<?> array) {
        List<Document> documents = new ArrayList<>();
        Document doc;
        for (Object item : array) {
            doc = parse(item);
            if (doc == null) {
                continue;
            }
            documents.add(doc);
        }
        return documents;
    }
    static List<Map<String, Object>> revert(Iterable<Document> documents) {
        List<Map<String, Object>> array = new ArrayList<>();
        for (Document doc : documents) {
            array.add(doc.toMap());
        }
        return array;
    }

    //
    //  Factory methods
    //

    /**
     *  Create from stored info
     */
    static Document create(String type, ID identifier, String data, TransportableData signature) {
        return SharedAccountExtensions.docHelper.createDocument(type, identifier, data, signature);
    }

    /**
     *  Create new empty document
     */
    static Document create(String type, ID identifier) {
        return SharedAccountExtensions.docHelper.createDocument(type, identifier, null, null);
    }

    static Document parse(Object doc) {
        return SharedAccountExtensions.docHelper.parseDocument(doc);
    }

    static Factory getFactory(String type) {
        return SharedAccountExtensions.docHelper.getDocumentFactory(type);
    }
    static void setFactory(String type, Factory factory) {
        SharedAccountExtensions.docHelper.setDocumentFactory(type, factory);
    }

    /**
     *  General Helper
     */
    interface Helper {

        void setDocumentFactory(String type, Factory factory);
        Factory getDocumentFactory(String type);

        Document createDocument(String type, ID identifier, String data, TransportableData signature);

        Document parseDocument(Object doc);

    }

    /**
     *  Document Factory
     */
    interface Factory {

        /**
         *  Create document
         *  <p>
         *      1. Create document with data & signature loaded from local storage
         *  </p>
         *  <p>
         *      2. Create a new empty document with entity ID only
         *  </p>
         *
         * @param identifier - entity ID
         * @param data       - document data (JsON)
         * @param signature  - document signature (Base64)
         * @return Document
         */
        Document createDocument(ID identifier, String data, TransportableData signature);

        /**
         *  Parse map object to entity document
         *
         * @param doc - info
         * @return Document
         */
        Document parseDocument(Map<String, Object> doc);
    }

}
