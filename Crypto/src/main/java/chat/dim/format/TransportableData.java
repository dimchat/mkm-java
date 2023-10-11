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
package chat.dim.format;

import java.util.Map;

import chat.dim.type.Mapper;

/**
 *  Transportable Data
 *  ~~~~~~~~~~~~~~~~~~
 *  TED - Transportable Encoded Data
 *
 *      0. "{BASE64_ENCODE}"
 *      1. "base64,{BASE64_ENCODE}"
 *      2. "data:image/png;base64,{BASE64_ENCODE}"
 *      3. {
 *              algorithm : "base65",
 *              data      : "...",     // base64_encode(data)
 *              ...
 *      }
 */
public interface TransportableData extends Mapper {

    //
    //  encode algorithm
    //
    String DEFAULT = "base64";
    String BASE_64 = "base64";
    String BASE_58 = "base58";
    String HEX     = "hex";

    /**
     *  Get data encode algorithm
     *
     * @return "base64"
     */
    String getAlgorithm();

    /**
     *  Get original data
     *
     * @return plaintext
     */
    byte[] getData();

    /**
     *  Get encoded string
     *
     * @return "{BASE64_ENCODE}", or
     *         "base64,{BASE64_ENCODE}", or
     *         "data:image/png;base64,{BASE64_ENCODE}", or
     *         "{...}"
     */
    @Override
    String toString();

    /**
     *  toJson()
     *
     * @return String, or Map
     */
    Object toObject();

    //
    //  Conveniences
    //

    static Object encode(byte[] data) {
        TransportableData ted = create(data);
        return ted.toObject();
    }

    static byte[] decode(Object encoded) {
        TransportableData ted = parse(encoded);
        return ted == null ? null : ted.getData();
    }

    //
    //  Factory methods
    //

    static TransportableData create(byte[] data) {
        return create(DEFAULT, data);
    }
    static TransportableData create(String algorithm, byte[] data) {
        FormatFactoryManager man = FormatFactoryManager.getInstance();
        return man.generalFactory.createTransportableData(algorithm, data);
    }

    static TransportableData parse(Object ted) {
        FormatFactoryManager man = FormatFactoryManager.getInstance();
        return man.generalFactory.parseTransportableData(ted);
    }

    static void setFactory(String algorithm, Factory factory) {
        FormatFactoryManager man = FormatFactoryManager.getInstance();
        man.generalFactory.setTransportableDataFactory(algorithm, factory);
    }
    static Factory getFactory(String algorithm) {
        FormatFactoryManager man = FormatFactoryManager.getInstance();
        return man.generalFactory.getTransportableDataFactory(algorithm);
    }

    /**
     *  TED Factory
     *  ~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Create TED
         *
         * @param data - original data
         * @return TED object
         */
        TransportableData createTransportableData(byte[] data);

        /**
         *  Parse string/map object to TED
         *
         * @param ted - TED info
         * @return TED object
         */
        TransportableData parseTransportableData(Map<String, Object> ted);
    }
}
