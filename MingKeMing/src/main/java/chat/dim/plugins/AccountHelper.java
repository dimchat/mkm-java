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
package chat.dim.plugins;

import java.util.List;
import java.util.Map;

import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;
import chat.dim.format.TransportableData;
import chat.dim.protocol.Address;
import chat.dim.protocol.Document;
import chat.dim.protocol.ID;
import chat.dim.protocol.Meta;

/**
 *  Account GeneralFactory
 *  ~~~~~~~~~~~~~~~~~~~~~~
 */
public interface AccountHelper {

    //
    //  Address
    //

    void setAddressFactory(Address.Factory factory);

    Address.Factory getAddressFactory();

    Address parseAddress(Object address);

    Address createAddress(String address);

    Address generateAddress(Meta meta, int network);

    //
    //  ID
    //

    void setIdentifierFactory(ID.Factory factory);

    ID.Factory getIdentifierFactory();

    ID parseIdentifier(Object identifier);

    ID createIdentifier(String name, Address address, String terminal);

    ID generateIdentifier(Meta meta, int network, String terminal);

    List<ID> convertIdentifiers(Iterable<?> members);

    List<String> revertIdentifiers(Iterable<ID> members);

    //
    //  Meta
    //

    void setMetaFactory(String type, Meta.Factory factory);

    Meta.Factory getMetaFactory(String type);

    String getMetaType(Map<?, ?> meta, String defaultValue);

    Meta createMeta(String type, VerifyKey key, String seed, TransportableData fingerprint);

    Meta generateMeta(String type, SignKey sKey, String seed);

    Meta parseMeta(Object meta);

    //
    //  Document
    //

    void setDocumentFactory(String type, Document.Factory factory);

    Document.Factory getDocumentFactory(String type);

    String getDocumentType(Map<?, ?> doc, String defaultValue);

    Document createDocument(String type, ID identifier, String data, TransportableData signature);

    Document parseDocument(Object doc);

}
