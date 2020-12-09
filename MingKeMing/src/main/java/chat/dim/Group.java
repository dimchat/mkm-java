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

import java.util.ArrayList;
import java.util.List;

import chat.dim.protocol.Bulletin;
import chat.dim.protocol.Document;
import chat.dim.protocol.ID;

public class Group extends Entity {

    private ID founder = null;

    public Group(ID identifier) {
        super(identifier);
    }

    @Override
    public DataSource getDataSource() {
        return (DataSource) super.getDataSource();
    }

    public ID getFounder() {
        if (founder == null) {
            founder = getDataSource().getFounder(identifier);
            if (founder == null && ID.isBroadcast(identifier)) {
                // founder of broadcast group
                String name = identifier.getName();
                int len = name == null ? 0 : name.length();
                if (len == 0 || (len == 8 && name.equalsIgnoreCase("everyone"))) {
                    // Consensus: the founder of group 'everyone@everywhere'
                    //            'Albert Moky'
                    founder = ID.parse("moky@anywhere");
                } else {
                    // DISCUSS: who should be the founder of group 'xxx@everywhere'?
                    //          'anyone@anywhere', or 'xxx.founder@anywhere'
                    founder = ID.parse(name + ".founder@anywhere");
                }
            }
        }
        return founder;
    }

    public ID getOwner() {
        ID owner = getDataSource().getOwner(identifier);
        if (owner == null && ID.isBroadcast(identifier)) {
            // owner of broadcast group
            String name = identifier.getName();
            int len = name == null ? 0 : name.length();
            if (len == 0 || (len == 8 && name.equalsIgnoreCase("everyone"))) {
                // Consensus: the owner of group 'everyone@everywhere'
                //            'anyone@anywhere'
                owner = ID.ANYONE;
            } else {
                // DISCUSS: who should be the owner of group 'xxx@everywhere'?
                //          'anyone@anywhere', or 'xxx.owner@anywhere'
                owner = ID.parse(name + ".owner@anywhere");
            }
        }
        return owner;
    }

    public List<ID> getMembers() {
        List<ID> members = getDataSource().getMembers(identifier);
        if (members == null && ID.isBroadcast(identifier)) {
            // members of broadcast group
            ID member;
            ID owner;
            String name = identifier.getName();
            int len = name == null ? 0 : name.length();
            if (len == 0 || (len == 8 && name.equalsIgnoreCase("everyone"))) {
                // Consensus: the member of group 'everyone@everywhere'
                //            'anyone@anywhere'
                member = ID.ANYONE;
                owner = ID.ANYONE;
            } else {
                // DISCUSS: who should be the member of group 'xxx@everywhere'?
                //          'anyone@anywhere', or 'xxx.member@anywhere'
                member = ID.parse(name + ".member@anywhere");
                owner = ID.parse(name + ".owner@anywhere");
            }
            // add owner first
            members = new ArrayList<>();
            if (owner != null) {
                members.add(owner);
            }
            // check and add member
            if (member != null && !member.equals(owner)) {
                members.add(member);
            }
        }
        return members;
    }

    public List<ID> getAssistants() {
        Document doc = getDocument(Document.BULLETIN);
        if (doc instanceof Bulletin) {
            return ((Bulletin) doc).getAssistants();
        }
        return null;
    }

    /**
     *  Group Data Source
     *  ~~~~~~~~~~~~~~~~~
     */
    public interface DataSource extends Entity.DataSource {

        /**
         *  Get group founder
         *
         * @param group - group ID
         * @return fonder ID
         */
        ID getFounder(ID group);

        /**
         *  Get group owner
         *
         * @param group - group ID
         * @return owner ID
         */
        ID getOwner(ID group);

        /**
         *  Get group members list
         *
         * @param group - group ID
         * @return members list (ID)
         */
        List<ID> getMembers(ID group);
    }
}
