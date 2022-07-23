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
import java.util.Set;

public interface Wrapper {

    /**
     *  Get inner String
     *  ~~~~~~~~~~~~~~~~
     *  Remove first wrapper
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
     *  Get inner Map
     *  ~~~~~~~~~~~~~
     *  Remove first wrapper
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
     *  Unwrap recursively
     *  ~~~~~~~~~~~~~~~~~~
     *  Remove all wrappers
     */
    @SuppressWarnings("unchecked")
    static Object unwrap(Object object) {
        if (object instanceof Stringer) {
            return object.toString();
        } else if (object instanceof Map) {
            return unwrapMap((Map<String, Object>) object);
        } else if (object instanceof List) {
            return unwrapList((List<Object>) object);
        } else {
            return object;
        }
    }

    static Map<String, Object> unwrapMap(Map<String, Object> dict) {
        if (dict instanceof Mapper) {
            dict = ((Mapper) dict).toMap();
        }
        Map<String, Object> result = new HashMap<>();
        Set<String> allKeys = dict.keySet();
        Object naked, value;
        for (String key : allKeys) {
            value = dict.get(key);
            naked = unwrap(value);
            result.put(key, naked);
        }
        return result;
    }
    static List<Object> unwrapList(List<Object> array) {
        List<Object> result = new ArrayList<>();
        Object naked;
        for (Object item : array) {
            naked = unwrap(item);
            result.add(naked);
        }
        return result;
    }
}
