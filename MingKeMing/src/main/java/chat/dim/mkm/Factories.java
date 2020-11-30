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
package chat.dim.mkm;

import java.util.Map;

import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;
import chat.dim.protocol.Address;
import chat.dim.protocol.Document;
import chat.dim.protocol.ID;
import chat.dim.protocol.Meta;

public final class Factories {

    public static Address.Factory addressFactory = new AddressFactory() {

        @Override
        protected Address createAddress(String address) {
            throw new UnsupportedOperationException("implement me!");
        }
    };

    public static ID.Factory idFactory = new IDFactory();

    public static Meta.Factory metaFactory = new Meta.Factory() {

        @Override
        public Meta createMeta(int version, VerifyKey key, String seed, byte[] fingerprint) {
            throw new UnsupportedOperationException("implement me!");
        }

        @Override
        public Meta generateMeta(int version, SignKey sKey, String seed) {
            throw new UnsupportedOperationException("implement me!");
        }

        @Override
        public Meta parseMeta(Map<String, Object> meta) {
            throw new UnsupportedOperationException("implement me!");
        }
    };

    public static Document.Factory documentFactory = new Document.Factory() {

        @Override
        public Document createDocument(ID identifier, String type, String data, String signature) {
            if (ID.isUser(identifier)) {
                if (type == null || Document.VISA.equals(type)) {
                    return new BaseVisa(identifier, data, signature);
                }
            } else if (ID.isGroup(identifier)) {
                return new BaseBulletin(identifier, data, signature);
            }
            return new BaseDocument(identifier, data, signature);
        }

        @Override
        public Document generateDocument(ID identifier, String type) {
            if (ID.isUser(identifier)) {
                if (type == null || Document.VISA.equals(type)) {
                    return new BaseVisa(identifier);
                }
            } else if (ID.isGroup(identifier)) {
                return new BaseBulletin(identifier);
            }
            return new BaseDocument(identifier, type);
        }

        @Override
        public Document parseDocument(Map<String, Object> doc) {
            ID identifier = ID.parse(doc.get("ID"));
            if (identifier == null) {
                return null;
            }
            if (ID.isUser(identifier)) {
                String type = (String) doc.get("type");
                if (type == null || Document.VISA.equals(type)) {
                    return new BaseVisa(doc);
                }
            } else if (ID.isGroup(identifier)) {
                return new BaseBulletin(doc);
            }
            return new BaseDocument(doc);
        }
    };
}
