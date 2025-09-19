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
package chat.dim.protocol;

import java.util.Map;

import chat.dim.ext.SharedCryptoExtensions;

/**
 *  Symmetric Cryptography Key
 *  <p>
 *      This class is used to encrypt or decrypt message data
 *  </p>
 *
 *  <blockquote><pre>
 *  key data format: {
 *      algorithm : "AES", // "DES", ...
 *      data      : "{BASE64_ENCODE}",
 *      ...
 *  }
 *  </pre></blockquote>
 */
public interface SymmetricKey extends EncryptKey, DecryptKey {

    /*
    String AES = "AES"; //-- "AES/CBC/PKCS7Padding"
    String DES = "DES";
     */

    //
    //  Factory methods
    //
    static SymmetricKey generate(String algorithm) {
        return SharedCryptoExtensions.symmetricHelper.generateSymmetricKey(algorithm);
    }
    static SymmetricKey parse(Object key) {
        return SharedCryptoExtensions.symmetricHelper.parseSymmetricKey(key);
    }

    static Factory getFactory(String algorithm) {
        return SharedCryptoExtensions.symmetricHelper.getSymmetricKeyFactory(algorithm);
    }
    static void setFactory(String algorithm, Factory factory) {
        SharedCryptoExtensions.symmetricHelper.setSymmetricKeyFactory(algorithm, factory);
    }

    /**
     *  Key Factory
     */
    interface Factory {

        /**
         *  Generate key
         *
         * @return SymmetricKey
         */
        SymmetricKey generateSymmetricKey();

        /**
         *  Parse map object to key
         *
         * @param key
         *        key info
         *
         * @return SymmetricKey
         */
        SymmetricKey parseSymmetricKey(Map<String, Object> key);
    }

}
