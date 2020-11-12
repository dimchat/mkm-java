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
package chat.dim.protocol;

import chat.dim.crypto.EncryptKey;

/**
 *  User/Group Profile data
 *  ~~~~~~~~~~~~~~~~~~~~~~~
 *  This class is used to generate entity profile
 *
 *      data format: {
 *          ID: "EntityID",   // entity ID
 *          data: "{JSON}",   // data = json_encode(info)
 *          signature: "..."  // signature = sign(data, SK);
 *      }
 */
public interface Profile extends TAI {

    /**
     *  Get entity ID
     *
     * @return entity ID
     */
    ID getIdentifier();

    //---- properties getter/setter

    /**
     *  Get entity name
     *
     * @return name string
     */
    String getName();

    void setName(String value);

    /**
     *  Get public key to encrypt message for user
     *
     * @return public key
     */
    EncryptKey getKey();

    /**
     *  Set public key for other user to encrypt message
     *
     * @param publicKey - public key in profile.key
     */
    void setKey(EncryptKey publicKey);

    /**
     *  Profile Parser
     *  ~~~~~~~~~~~~~~
     */
    interface Parser {

        /**
         *  Parse map object to profile
         *
         * @param profile - profile info
         * @return Profile
         */
        Profile parseProfile(Object profile);
    }
}