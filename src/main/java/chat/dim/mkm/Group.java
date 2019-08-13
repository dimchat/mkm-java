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

import java.util.List;

public class Group extends Entity {

    public Group(ID identifier) {
        super(identifier);
    }

    public ID getFounder() {
        if (dataSource == null) {
            return null;
        }
        GroupDataSource delegate = (GroupDataSource) dataSource;
        return delegate.getFounder(identifier);
    }

    public ID getOwner() {
        if (dataSource == null) {
            return null;
        }
        GroupDataSource delegate = (GroupDataSource) dataSource;
        return delegate.getOwner(identifier);
    }

    public List<ID> getMembers() {
        if (dataSource == null) {
            return null;
        }
        GroupDataSource delegate = (GroupDataSource) dataSource;
        return delegate.getMembers(identifier);
    }

    @Override
    public Profile getProfile() {
        Profile profile = super.getProfile();
        if (profile == null || profile.isValid() || dataSource == null) {
            // no need to verify
            return profile;
        }
        // try to verify with owner's meta.key
        ID owner = getOwner();
        Meta meta = dataSource.getMeta(owner);
        if (meta != null && profile.verify(meta.key)) {
            // signature correct
            return profile;
        }
        // profile error? continue to process by subclass
        return profile;
    }
}
