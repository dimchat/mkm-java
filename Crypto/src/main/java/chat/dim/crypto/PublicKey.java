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

import chat.dim.plugins.SharedCryptoExtensions;

/**
 *  Asymmetric Cryptography Public Key
 *
 *  <blockquote><pre>
 *  key data format: {
 *      algorithm : "RSA", // "ECC", ...
 *      data      : "{BASE64_ENCODE}",
 *      ...
 *  }
 *  </pre></blockquote>
 */
public interface PublicKey extends VerifyKey {

    //
    //  Factory method
    //
    static PublicKey parse(Object key) {
        return SharedCryptoExtensions.publicHelper.parsePublicKey(key);
    }

    static Factory getFactory(String algorithm) {
        return SharedCryptoExtensions.publicHelper.getPublicKeyFactory(algorithm);
    }
    static void setFactory(String algorithm, Factory factory) {
        SharedCryptoExtensions.publicHelper.setPublicKeyFactory(algorithm, factory);
    }

    /**
     *  Key Factory
     */
    interface Factory {

        /**
         *  Parse map object to key
         *
         * @param key
         *        key info
         *
         * @return PublicKey
         */
        PublicKey parsePublicKey(Map<String, Object> key);
    }

}
