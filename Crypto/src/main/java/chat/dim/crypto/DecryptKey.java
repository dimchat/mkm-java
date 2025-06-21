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
package chat.dim.crypto;

import java.util.Map;

public interface DecryptKey extends CryptographyKey {

    /**
     *  1. Symmetric Key:
     *     <blockquote><pre>
     *         plaintext = decrypt(ciphertext, PW);
     *     </pre></blockquote>
     *
     *  2. Asymmetric Private Key:
     *     <blockquote><pre>
     *         plaintext = decrypt(ciphertext, SK);
     *     </pre></blockquote>
     *
     * @param ciphertext
     *        encrypted data
     *
     * @param params
     *        extra params ('IV' for 'AES')
     *
     * @return plaintext
     */
    byte[] decrypt(byte[] ciphertext, Map<String, Object> params);

    /**
     *  Check symmetric keys by encryption.
     *  <blockquote><pre>
     *      CT = encrypt(data, PK);
     *      OK = decrypt(CT, SK) == data;
     *  </pre></blockquote>
     *
     * @param pKey
     *        encrypt (public) key
     *
     * @return true on signature matched
     */
    boolean matchEncryptKey(EncryptKey pKey);
}
