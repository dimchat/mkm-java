/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Albert Moky
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
package chat.dim.plugins;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import chat.dim.crypto.DecryptKey;
import chat.dim.crypto.EncryptKey;
import chat.dim.crypto.SignKey;
import chat.dim.crypto.VerifyKey;

/**
 *  CryptographyKey GeneralFactory
 */
public interface GeneralCryptoHelper /*extends SymmetricKey.Helper, PrivateKey.Helper, PublicKey.Helper */{

    // sample data for checking keys
    byte[] PROMISE = "Moky loves May Lee forever!".getBytes();

    /**
     *  Compare asymmetric keys
     */
    static boolean matchAsymmetricKeys(SignKey sKey, VerifyKey pKey) {
        // verify with signature
        byte[] signature = sKey.sign(PROMISE);
        return pKey.verify(PROMISE, signature);
    }

    /**
     *  Compare symmetric keys
     */
    static boolean matchSymmetricKeys(EncryptKey pKey, DecryptKey sKey) {
        // check by encryption
        Map<String, Object> params = new HashMap<>();
        byte[] ciphertext = pKey.encrypt(PROMISE, params);
        byte[] plaintext = sKey.decrypt(ciphertext, params);
        return Arrays.equals(plaintext, PROMISE);
    }

    //
    //  Algorithm
    //

    String getKeyAlgorithm(Map<?, ?> key, String defaultValueIfNull);

}
