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
import chat.dim.mkm.entity.EntityDataSource;

import java.util.List;

public interface UserDataSource extends EntityDataSource {

    int PRIVATE_KEY_SIGNATURE_FLAG = 1;
    int PRIVATE_KEY_DECRYPTION_FLAG = 2;

    /**
     *  Get user's private key
     *
     * @param flag - 1 for signature, 2 for decryption
     * @param user - user account
     * @return private key
     */
    PrivateKey getPrivateKey(int flag, User user);

    /**
     *  Get contacts list
     *
     * @param user - user account
     * @return contacts list
     */
    List<Object> getContacts(User user);

    /**
     *  Get contacts count
     *
     * @param user - user account
     * @return number of contacts
     */
    int getCountOfContacts(User user);

    /**
     *  Get contact ID at index
     *
     * @param index - contact index
     * @param user - user account
     * @return contact ID
     */
    ID getContactAtIndex(int index, User user);
}
