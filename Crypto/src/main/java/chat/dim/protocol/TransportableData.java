/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Albert Moky
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

import chat.dim.ext.SharedFormatExtensions;
import chat.dim.type.Stringer;

/**
 *  Transportable Data
 *  <p>
 *      TED - Transportable Encoded Data
 *  </p>
 *
 *  <blockquote><pre>
 *      0. "{BASE64_ENCODE}"
 *      1. "data:image/png;base64,{BASE64_ENCODE}"
 *  </pre></blockquote>
 */
public interface TransportableData extends Stringer, TransportableResource {

    /*
    //
    //  encoding algorithm
    //
    String DEFAULT = "base64";
    String BASE_64 = "base64";
    String BASE_58 = "base58";
    String HEX     = "hex";
     */

    /**
     *  Get data encoding algorithm
     *
     * @return "base64"
     */
    String getEncoding();

    /**
     *  Get original data
     *
     * @return plaintext
     */
    byte[] getBytes();

    /**
     *  Get encoded string
     *
     * @return "{BASE64_ENCODE}", or
     *         "data:image/png;base64,{BASE64_ENCODE}"
     */
    @Override
    String toString();

    /**
     *  toString()
     */
    @Override
    Object serialize();

    //
    //  Factory method
    //

    static TransportableData parse(Object ted) {
        return SharedFormatExtensions.tedHelper.parseTransportableData(ted);
    }

    static void setFactory(Factory factory) {
        SharedFormatExtensions.tedHelper.setTransportableDataFactory(factory);
    }
    static Factory getFactory() {
        return SharedFormatExtensions.tedHelper.getTransportableDataFactory();
    }

    /**
     *  TED Factory
     */
    interface Factory {

        /**
         *  Parse string object to TED
         *
         * @param ted - TED string
         * @return TED object
         */
        TransportableData parseTransportableData(String ted);
    }

}
