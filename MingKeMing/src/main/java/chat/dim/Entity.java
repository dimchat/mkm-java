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

import chat.dim.mkm.EntityParser;
import chat.dim.protocol.Address;
import chat.dim.protocol.ID;
import chat.dim.protocol.Meta;
import chat.dim.protocol.Profile;

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
 *      profile    - entity profile
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
        return "<" + clazzName + "|" + identifier + " \"" + getName() + "\">";
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

    /**
     *  Get entity name
     *
     * @return name string
     */
    public String getName() {
        // get from profile
        Profile profile = getProfile();
        if (profile != null) {
            String name = profile.getName();
            if (name != null && name.length() > 0) {
                return name;
            }
        }
        // get ID.name
        return identifier.getName();
    }

    public Meta getMeta() {
        return getDataSource().getMeta(identifier);
    }

    public Profile getProfile() {
        return getDataSource().getProfile(identifier);
    }

    /**
     *  Entituy Parser
     *  ~~~~~~~~~~~~~~
     */
    public interface Parser extends ID.Parser, Address.Parser, Meta.Parser, Profile.Parser {

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
    public static Meta parseMeta(Object meta) {
        return parser.parseMeta(meta);
    }

    /**
     *  Parse map object to profile
     *
     * @param profile - profile info
     * @return Profile
     */
    public static Profile parseProfile(Object profile) {
        return parser.parseProfile(profile);
    }
}
