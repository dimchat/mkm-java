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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.dim.crypto.DecryptKey;
import chat.dim.type.Converter;
import chat.dim.type.Mapper;

/**
 *  Format GeneralFactory
 *  ~~~~~~~~~~~~~~~~~~~~~
 */
public class GeneralFactory {

    private final Map<String, TransportableData.Factory> tedFactories = new HashMap<>();

    private PortableNetworkFile.Factory pnfFactory = null;

    public List<String> split(String text) {
        List<String> array = new ArrayList<>();
        // "{TEXT}", or
        // "base64,{BASE64_ENCODE}", or
        // "data:image/png;base64,{BASE64_ENCODE}"
        int pos1 = text.indexOf("://");
        if (pos1 > 0) {
            // [URL]
            array.add(text);
            return array;
        }
        pos1 = text.indexOf(';') + 1;
        int pos2 = text.indexOf(',', pos1);
        if (pos2 < 0) {
            // [data]
            if (pos1 == 0) {
                array.add(text);
            } else {
                array.add(text.substring(pos1));
            }
        } else {
            // [data, algorithm]
            array.add(text.substring(pos2 + 1));
            array.add(text.substring(pos1, pos2));
        }
        return array;
    }
    @SuppressWarnings("unchecked")
    public Map<String, Object> decode(Object data, String defaultKey) {
        if (data instanceof Mapper) {
            return ((Mapper) data).toMap();
        } else if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        String text = data instanceof String ? (String) data : data.toString();
        if (text.startsWith("{") && text.endsWith("}")) {
            return JSONMap.decode(text);
        }
        Map<String, Object> info = new HashMap<>();
        List<String> array = split(text);
        if (array.size() == 1) {
            info.put(defaultKey, array.get(0));
        } else {
            assert array.size() == 2 : "split error: " + text + " => " + array;
            info.put("algorithm", array.get(1));
            info.put("data", array.get(0));
        }
        return info;
    }

    ///
    ///   TED - Transportable Encoded Data
    ///

    public String getDataAlgorithm(Map<?, ?> ted, String defaultValue) {
        return Converter.getString(ted.get("algorithm"), defaultValue);
    }

    public void setTransportableDataFactory(String algorithm, TransportableData.Factory factory) {
        tedFactories.put(algorithm, factory);
    }
    public TransportableData.Factory getTransportableDataFactory(String algorithm) {
        return tedFactories.get(algorithm);
    }

    public TransportableData createTransportableData(String algorithm, byte[] data) {
        TransportableData.Factory factory = getTransportableDataFactory(algorithm);
        assert factory != null : "TED algorithm not support: " + algorithm;
        return factory.createTransportableData(data);
    }

    public TransportableData parseTransportableData(Object ted) {
        if (ted == null) {
            return null;
        } else if (ted instanceof TransportableData) {
            return (TransportableData) ted;
        }
        // unwrap
        Map<String, Object> info = decode(ted, "data");
        if (info == null) {
            assert false : "TED error: " + ted;
            return null;
        }
        String algorithm = getDataAlgorithm(info, "*");
        TransportableData.Factory factory = getTransportableDataFactory(algorithm);
        if (factory == null && !algorithm.equals("*")) {
            factory = getTransportableDataFactory("*");  // unknown
        }
        assert factory != null : "cannot parse TED: " + ted;
        return factory.parseTransportableData(info);
    }

    ///
    ///   PNF - Portable Network File
    ///

    public void setPortableNetworkFileFactory(PortableNetworkFile.Factory factory) {
        pnfFactory = factory;
    }
    public PortableNetworkFile.Factory getPortableNetworkFileFactory() {
        return pnfFactory;
    }

    public PortableNetworkFile createPortableNetworkFile(byte[] data, String filename,
                                                         URI url, DecryptKey password) {
        PortableNetworkFile.Factory factory = getPortableNetworkFileFactory();
        assert factory != null : "PNF factory not ready";
        return factory.createPortableNetworkFile(data, filename, url, password);
    }

    public PortableNetworkFile parsePortableNetworkFile(Object pnf) {
        if (pnf == null) {
            return null;
        } else if (pnf instanceof PortableNetworkFile) {
            return (PortableNetworkFile) pnf;
        }
        // unwrap
        Map<String, Object> info = decode(pnf, "URL");
        if (info == null) {
            assert false : "PNF error: " + pnf;
            return null;
        }
        PortableNetworkFile.Factory factory = getPortableNetworkFileFactory();
        assert factory != null : "PNF factory not ready";
        return factory.parsePortableNetworkFile(info);
    }

}
