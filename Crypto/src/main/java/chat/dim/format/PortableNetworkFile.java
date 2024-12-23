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

import java.net.URI;
import java.util.Map;

import chat.dim.crypto.DecryptKey;
import chat.dim.plugins.SharedFormatHolder;
import chat.dim.type.Mapper;

/**
 *  Transportable File
 *  ~~~~~~~~~~~~~~~~~~
 *  PNF - Portable Network File
 *
 *      0.  "{URL}"
 *      1. "base64,{BASE64_ENCODE}"
 *      2. "data:image/png;base64,{BASE64_ENCODE}"
 *      3. {
 *              data     : "...",        // base64_encode(fileContent)
 *              filename : "avatar.png",
 *
 *              URL      : "http://...", // download from CDN
 *              // before fileContent uploaded to a public CDN,
 *              // it can be encrypted by a symmetric key
 *              key      : {             // symmetric key to decrypt file content
 *                  algorithm : "AES",   // "DES", ...
 *                  data      : "{BASE64_ENCODE}",
 *                  ...
 *              }
 *      }
 */
public interface PortableNetworkFile extends Mapper {

    /** When file data is too big, don't set it in this dictionary,
     *  but upload it to a CDN and set the download URL instead.
     */
    void setData(byte[] data);
    byte[] getData();

    void setFilename(String filename);
    String getFilename();

    /** Download URL
     */
    void setURL(URI url);
    URI getURL();

    /** Password for decrypting the downloaded data from CDN,
     *  default is a plain key, which just return the same data when decrypting.
     */
    void setPassword(DecryptKey key);
    DecryptKey getPassword();

    /** Get encoded string
     *
     * @return "URL", or
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
    //  Factory methods
    //

    /**
     *  Create from remote URL
     */
    static PortableNetworkFile create(URI url, DecryptKey password) {
        return create(null, null, url, password);
    }
    /**
     *  Create from file data
     */
    static PortableNetworkFile create(byte[] data, String filename) {
        TransportableData ted = TransportableData.create(data);
        return create(ted, filename, null, null);
    }

    static PortableNetworkFile create(TransportableData data, String filename, URI url, DecryptKey password) {
        return SharedFormatHolder.pnfHelper.createPortableNetworkFile(data, filename, url, password);
    }

    static PortableNetworkFile parse(Object pnf) {
        return SharedFormatHolder.pnfHelper.parsePortableNetworkFile(pnf);
    }

    static void setFactory(Factory factory) {
        SharedFormatHolder.pnfHelper.setPortableNetworkFileFactory(factory);
    }
    static Factory getFactory() {
        return SharedFormatHolder.pnfHelper.getPortableNetworkFileFactory();
    }

    /**
     *  General Helper
     *  ~~~~~~~~~~~~~~
     */
    interface Helper {

        void setPortableNetworkFileFactory(Factory factory);
        Factory getPortableNetworkFileFactory();

        PortableNetworkFile parsePortableNetworkFile(Object pnf);

        PortableNetworkFile createPortableNetworkFile(TransportableData data, String filename,
                                                      URI url, DecryptKey password);

    }

    /**
     *  PNF Factory
     *  ~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Create PNF
         *
         * @param data     - file content (not encrypted)
         * @param filename - file name
         * @param url      - download URL
         * @param password - decrypt key for downloaded data
         * @return PNF object
         */
        PortableNetworkFile createPortableNetworkFile(TransportableData data, String filename,
                                                      URI url, DecryptKey password);

        /**
         *  Parse string/map object to PNF
         *
         * @param pnf - PNF info
         * @return PNF object
         */
        PortableNetworkFile parsePortableNetworkFile(Map<String, Object> pnf);
    }

}
