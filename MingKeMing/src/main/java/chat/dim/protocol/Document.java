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
    String ANY      = "";
    String VISA     = "visa";      // for login/communication
    String PROFILE  = "profile";   // for user info
    String BULLETIN = "bulletin";  // for group info

    /**
     *  Get document type
     *
     * @return document type
     */
    String getType();

    /**
     *  Get entity ID
     *
     * @return entity ID
     */
    ID getIdentifier();

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
        return Factories.documentFactory.createDocument(identifier, type, data, signature);
    }
    static Document create(ID identifier, String type) {
        return Factories.documentFactory.createDocument(identifier, type);
    }
    static Document parse(Map<String, Object> doc) {
        if (doc == null) {
            return null;
        } else if (doc instanceof Document) {
            return (Document) doc;
        } else if (doc instanceof SOMap) {
            doc = ((SOMap) doc).getMap();
        }
        return Factories.documentFactory.parseDocument(doc);
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
         * @param type       - document type
         * @param data       - document data
         * @param signature  - document signature
         * @return Document
         */
        Document createDocument(ID identifier, String type, byte[] data, byte[] signature);

        /**
         *  Create empty document with entity ID & document type
         *
         * @param identifier - entity ID
         * @param type       - document type
         * @return Document
         */
        Document createDocument(ID identifier, String type);

        /**
         *  Parse map object to entity document
         *
         * @param doc - info
         * @return Document
         */
        Document parseDocument(Map<String, Object> doc);
    }
}
