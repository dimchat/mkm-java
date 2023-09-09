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
 *              URL      : "http://...", // download from CDN
 *              data     : "...",        // base64_encode(fileContent)
 *              filename : "avatar.png",
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

    // download URL
    void setURL(URI url);
    URI getURL();

    // when file data is too big, don't set it in this dictionary,
    // but upload it to a CDN and set the download URL instead.
    void setData(byte[] data);
    byte[] getData();

    void setFilename(String filename);
    String getFilename();

    // password for decrypting the downloaded data from CDN,
    // default is a plain key, which just return the same data when decrypting.
    void setPassword(DecryptKey key);
    DecryptKey getPassword();

    /**
     *  Get encoded string
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

    static PortableNetworkFile create(URI url, byte[] data, String filename, DecryptKey key) {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.createPortableNetworkFile(url, data, filename, key);
    }

    static PortableNetworkFile parse(Object pnf) {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.parsePortableNetworkFile(pnf);
    }

    static void setFactory(Factory factory) {
        FactoryManager man = FactoryManager.getInstance();
        man.generalFactory.setPortableNetworkFileFactory(factory);
    }
    static Factory getFactory() {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.getPortableNetworkFileFactory();
    }

    /**
     *  PNF Factory
     *  ~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Create PNF
         *
         * @param url      - download URL
         * @param data     - file content (not encrypted)
         * @param filename - file name
         * @param key      - decrypt key
         * @return PNF object
         */
        PortableNetworkFile createPortableNetworkFile(URI url, byte[] data, String filename, DecryptKey key);

        /**
         *  Parse string/map object to PNF
         *
         * @param pnf - PNF info
         * @return PNF object
         */
        PortableNetworkFile parsePortableNetworkFile(Map<String, Object> pnf);
    }
}
