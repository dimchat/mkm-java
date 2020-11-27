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

import java.util.List;

import chat.dim.protocol.Bulletin;
import chat.dim.protocol.ID;
import chat.dim.protocol.Profile;

public class Group extends Entity {

    private ID founder = null;

    public Group(ID identifier) {
        super(identifier);
    }

    @Override
    public GroupDataSource getDataSource() {
        return (GroupDataSource) super.getDataSource();
    }

    public ID getFounder() {
        if (founder == null) {
            founder = getDataSource().getFounder(identifier);
        }
        return founder;
    }

    public ID getOwner() {
        return getDataSource().getOwner(identifier);
    }

    public List<ID> getMembers() {
        return getDataSource().getMembers(identifier);
    }

    public List<ID> getAssistants() {
        Profile profile = getProfile(Profile.BULLETIN);
        if (profile instanceof Bulletin) {
            return ((Bulletin) profile).getAssistants();
        }
        return null;
    }
}
