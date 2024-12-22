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
package chat.dim.plugins;

import java.net.URI;
import java.util.Map;

import chat.dim.crypto.DecryptKey;
import chat.dim.format.PortableNetworkFile;
import chat.dim.format.TransportableData;

/**
 *  Format GeneralFactory
 *  ~~~~~~~~~~~~~~~~~~~~~
 */
public interface FormatHelper {

    ///
    ///   TED - Transportable Encoded Data
    ///

    String getFormatAlgorithm(Map<?, ?> ted, String defaultValue);

    void setTransportableDataFactory(String algorithm, TransportableData.Factory factory);

    TransportableData.Factory getTransportableDataFactory(String algorithm);

    TransportableData createTransportableData(String algorithm, byte[] data);

    TransportableData parseTransportableData(Object ted);

    ///
    ///   PNF - Portable Network File
    ///

    void setPortableNetworkFileFactory(PortableNetworkFile.Factory factory);

    PortableNetworkFile.Factory getPortableNetworkFileFactory();

    PortableNetworkFile createPortableNetworkFile(TransportableData data, String filename, URI url, DecryptKey password);

    PortableNetworkFile parsePortableNetworkFile(Object pnf);

}
