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
package chat.dim.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Wrapper {

    /**
     *  Fetch String
     */
    static String getString(Object str) {
        if (str instanceof Stringer) {
            return str.toString();
        } else if (str instanceof String) {
            return (String) str;
        } else {
            return null;
        }
    }

    /**
     *  Fetch Map
     */
    @SuppressWarnings("unchecked")
    static Map<String, Object> getMap(Object dict) {
        if (dict instanceof Mapper) {
            return ((Mapper) dict).toMap();
        } else if (dict instanceof Map) {
            return (Map<String, Object>) dict;
        } else {
            return null;
        }
    }

    /**
     *  Deep Copy
     *  ~~~~~~~~~
     */
    @SuppressWarnings("unchecked")
    static Object unwrap(Object object) {
        if (object instanceof Stringer) {
            return object.toString();
        } else if (object instanceof Map) {  // Mapper
            return unwrapMap((Map<String, Object>) object);
        } else if (object instanceof List) {
            return unwrapList((List<Object>) object);
        } else {
            return object;
        }
    }

    static Object unwrapMap(Map<String, Object> dict) {
        Map<String, Object> clone = new HashMap<>();
        for (Map.Entry<String, Object> entry : dict.entrySet()) {
            clone.put(entry.getKey(), unwrap(entry.getValue()));
        }
        return clone;
    }
    static List<Object> unwrapList(List<Object> array) {
        List<Object> clone = new ArrayList<>();
        for (Object item : array) {
            clone.add(unwrap(item));
        }
        return clone;
    }
}
