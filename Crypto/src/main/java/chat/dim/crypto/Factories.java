/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Albert Moky
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

public final class Factories {

    public static SymmetricKey.Factory symmetricKeyFactory = new SymmetricKey.Factory() {

        @Override
        public SymmetricKey generateSymmetricKey(String algorithm) {
            throw new UnsupportedOperationException("implement me!");
        }

        @Override
        public SymmetricKey parseSymmetricKey(Map<String, Object> key) {
            throw new UnsupportedOperationException("implement me!");
        }
    };

    public static PublicKey.Factory publicKeyFactory = new PublicKey.Factory() {

        @Override
        public PublicKey parsePublicKey(Map<String, Object> key) {
            throw new UnsupportedOperationException("implement me!");
        }
    };

    public static PrivateKey.Factory privateKeyFactory = new PrivateKey.Factory() {

        @Override
        public PrivateKey generatePrivateKey(String algorithm) {
            throw new UnsupportedOperationException("implement me!");
        }

        @Override
        public PrivateKey parsePrivateKey(Map<String, Object> key) {
            throw new UnsupportedOperationException("implement me!");
        }
    };
}
