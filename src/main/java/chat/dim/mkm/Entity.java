/* license: https://mit-license.org
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
package chat.dim.mkm;

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

    public EntityDataSource dataSource;

    public Entity(ID identifier) {
        super();
        this.identifier = identifier;
        this.dataSource = null;
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
        return "<" + clazzName + "|" + getType() + " " + identifier + " (" + getNumber() + ") \"" + getName() + "\">";
    }

    /**
     *  Get entity type
     *
     * @return type
     */
    public NetworkType getType() {
        return identifier.getType();
    }

    /**
     *  Get Search Number
     *
     * @return number for searching this entity
     */
    public long getNumber() {
        return identifier.getNumber();
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
        return identifier.name;
    }

    public Meta getMeta() {
        if (dataSource == null) {
            throw new NullPointerException("entity data source not set yet: " + identifier);
        }
        return dataSource.getMeta(identifier);
    }

    public Profile getProfile() {
        if (dataSource == null) {
            throw new NullPointerException("entity data source not set yet: " + identifier);
        }
        return dataSource.getProfile(identifier);
    }
}
