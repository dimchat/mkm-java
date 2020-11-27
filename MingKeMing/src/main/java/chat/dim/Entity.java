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
package chat.dim;

import java.lang.ref.WeakReference;
import java.util.Map;

import chat.dim.protocol.Address;
import chat.dim.protocol.Document;
import chat.dim.protocol.ID;
import chat.dim.protocol.Meta;
import chat.dim.protocol.NetworkType;

/**
 *  Entity (User/Group)
 *  ~~~~~~~~~~~~~~~~~~~
 *  Base class of User and Group, ...
 *
 *  properties:
 *      identifier - entity ID
 *      type       - entity type
 *      number     - search number
 *      meta       - meta for generate ID
 *      document   - entity document
 *      name       - nickname
 */
public abstract class Entity {

    public final ID identifier;

    private WeakReference<EntityDataSource> dataSourceRef = null;

    public Entity(ID identifier) {
        super();
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            // same object
            return true;
        } else if (other instanceof Entity) {
            // check with identifier
            Entity entity = (Entity) other;
            return identifier.equals(entity.identifier);
        } else {
            // null or unknown object
            return false;
        }
    }

    @Override
    public String toString() {
        String clazzName = getClass().getSimpleName();
        return "<" + clazzName + "|" + getType() + " " + identifier + " \"" + getName() + "\">";
    }

    /**
     *  Get ID.type
     *
     * @return network type
     */
    public byte getType() {
        return identifier.getType();
    }

    public EntityDataSource getDataSource() {
        if (dataSourceRef == null) {
            return null;
        }
        return dataSourceRef.get();
    }

    public void setDataSource(EntityDataSource dataSource) {
        dataSourceRef = new WeakReference<>(dataSource);
    }

    public Meta getMeta() {
        return getDataSource().getMeta(identifier);
    }

    public Document getDocument(String type) {
        return getDataSource().getDocument(identifier, type);
    }

    /**
     *  Get entity name
     *
     * @return name string
     */
    public String getName() {
        // get from document
        Document doc = null;
        if (NetworkType.isUser(identifier.getType())) {
            doc = getDocument(Document.PROFILE);
            if (doc == null) {
                doc = getDocument(Document.VISA);
            }
        } else if (NetworkType.isGroup(identifier.getType())) {
            doc = getDocument(Document.BULLETIN);
        }
        if (doc != null) {
            String name = doc.getName();
            if (name != null) {
                return name;
            }
        }
        // get ID.name
        return identifier.getName();
    }

    /**
     *  Entity Parser
     *  ~~~~~~~~~~~~~
     */
    public interface Parser extends ID.Parser, Address.Parser, Meta.Parser, Document.Parser {

    }

    // default parser
    public static Parser parser = new EntityParser() {
        @Override
        protected Meta createMeta(Map<String, Object> meta) {
            throw new UnsupportedOperationException("implement me!");
        }
    };

    /**
     *  Parse string object to ID
     *
     * @param identifier - ID string
     * @return ID
     */
    public static ID parseID(Object identifier) {
        return parser.parseID(identifier);
    }

    /**
     *  Parse map object to meta
     *
     * @param meta - meta info
     * @return Meta
     */
    public static Meta parseMeta(Map<String, Object> meta) {
        return parser.parseMeta(meta);
    }

    /**
     *  Parse map object to entity document
     *
     * @param doc - document info
     * @return Document
     */
    public static Document parseDocument(Map<String, Object> doc) {
        return parser.parseDocument(doc);
    }
}
