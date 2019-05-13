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

import chat.dim.mkm.entity.Entity;
import chat.dim.mkm.entity.ID;

import java.util.ArrayList;
import java.util.List;

public class Group extends Entity {

    private final ID founder;

    public Group(ID identifier, ID founder) {
        super(identifier);
        this.founder = founder;
    }

    public Group(ID identifier) {
        this(identifier, null);
    }

    public ID getFounder() {
        if (this.founder != null) {
            return this.founder;
        }
        if (this.dataSource == null) {
            return null;
        }
        // get from data source
        GroupDataSource dataSource = (GroupDataSource) this.dataSource;
        return dataSource.getFounder(this);
    }

    public ID getOwner() {
        if (this.dataSource == null) {
            return null;
        }
        // get from data source
        GroupDataSource dataSource = (GroupDataSource) this.dataSource;
        return dataSource.getOwner(this);
    }

    public List<Object> getMembers() {
        if (this.dataSource == null) {
            return null;
        }
        // get from data source
        GroupDataSource dataSource = (GroupDataSource) this.dataSource;
        List<Object> members = dataSource.getMembers(this);
        if (members != null) {
            return members;
        }
        int count = dataSource.getCountOfMembers(this);
        if (count == 0) {
            return null;
        } else if (count < 0) {
            throw new ArrayIndexOutOfBoundsException("failed to get members of group:" + identifier);
        }
        // get members one by one
        members = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            members.add(dataSource.getMemberAtIndex(index, this));
        }
        return members;
    }
}
