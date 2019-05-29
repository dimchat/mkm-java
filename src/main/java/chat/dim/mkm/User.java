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

import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.entity.ID;

import java.util.ArrayList;
import java.util.List;

public class User extends Account {

    public User(ID identifier) {
        super(identifier);
    }

    /**
     *  Get all contacts of the user
     *
     * @return contact list
     */
    public List<Object> getContacts() {
        if (this.dataSource == null) {
            return null;
        }
        UserDataSource dataSource = (UserDataSource) this.dataSource;
        List<Object> contacts = dataSource.getContacts(this);
        if (contacts != null) {
            return contacts;
        }
        int count = dataSource.getCountOfContacts(this);
        if (count == 0) {
            return null;
        } else if (count < 0) {
            throw new ArrayIndexOutOfBoundsException("failed to get contacts of user:" + identifier);
        }
        contacts = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            contacts.add(dataSource.getContactAtIndex(index, this));
        }
        return contacts;
    }

    /**
     *  Sign data with user's private key
     *
     * @param data - message data
     * @return signature
     */
    public byte[] sign(byte[] data) {
        if (this.dataSource == null) {
            throw new NullPointerException("data source not set for user:" + identifier);
        }
        // get from data source
        UserDataSource dataSource = (UserDataSource) this.dataSource;
        PrivateKey privateKey = dataSource.getPrivateKey(UserDataSource.PRIVATE_KEY_SIGNATURE_FLAG, this);
        if (privateKey == null) {
            throw new NullPointerException("failed to get private key for user:" + identifier);
        }
        return privateKey.sign(data);
    }

    /**
     *  Decrypt data with user's private key
     *
     * @param ciphertext - encrypted data
     * @return plain text
     */
    public byte[] decrypt(byte[] ciphertext) {
        if (this.dataSource == null) {
            throw new NullPointerException("data source not set for user:" + identifier);
        }
        // get from data source
        UserDataSource dataSource = (UserDataSource) this.dataSource;
        PrivateKey privateKey = dataSource.getPrivateKey(UserDataSource.PRIVATE_KEY_DECRYPTION_FLAG, this);
        if (privateKey == null) {
            throw new NullPointerException("failed to get private key for user:" + identifier);
        }
        return privateKey.decrypt(ciphertext);
    }
}
