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

import java.util.Map;

import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;

/**
 *  The Additional Information
 *
 *  <pre>
 *  'Meta' is the information for entity which never changed,
 *      which contains the key for verify signature;
 *  'TAI' is the variable part,
 *      which could contain a public key for asymmetric encryption.
 *  </pre>
 */
public interface TAI {

    /**
     *  Check if signature matched
     *
     * @return False on signature not matched
     */
    boolean isValid();

    //-------- signature

    /**
     *  Verify 'data' and 'signature' with public key
     *
     * @param metaKey - public key in meta.key
     * @return true on signature matched
     */
    boolean verify(VerifyKey metaKey);

    /**
     *  Encode properties to 'data' and sign it to 'signature'
     *
     * @param sKey - private key match meta.key
     * @return signature, null on error
     */
    byte[] sign(SignKey sKey);

    //-------- properties

    /**
     *  Get all properties
     *
     * @return properties
     */
    Map<String, Object> getProperties();

    /**
     *  Get property data with key
     *
     * @param name - property name
     * @return property data
     */
    Object getProperty(String name);

    /**
     *  Update property with key and data
     *  <p>
     *      (this will reset 'data' and 'signature')
     *  </p>
     *
     * @param name  - property name
     * @param value - property data
     */
    void setProperty(String name, Object value);
}
