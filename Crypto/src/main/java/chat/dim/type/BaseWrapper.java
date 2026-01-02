/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Albert Moky
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

public class BaseWrapper implements DataWrapper {

    @Override
    public String getString(Object str) {
        if (str == null) {
            return null;
        } else if (str instanceof Stringer) {
            return str.toString();
        } else if (str instanceof String) {
            return (String) str;
        } else {
            assert false : "string error: " + str;
            return str.toString();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getMap(Object dict) {
        if (dict == null) {
            return null;
        } else if (dict instanceof Mapper) {
            return ((Mapper) dict).toMap();
        } else if (dict instanceof Map) {
            return (Map<String, Object>) dict;
        } else {
            assert false : "map error: " + dict;
            return null;
        }
    }

    @Override
    public Object unwrap(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Mapper) {
            return unwrapMap(((Mapper) object).toMap());
        } else if (object instanceof Map) {
            return unwrapMap((Map<?, ?>) object);
        } else if (object instanceof List) {
            return unwrapList((List<?>) object);
        } else if (object instanceof Stringer) {
            return object.toString();
        } else {
            return object;
        }
    }

    @Override
    public Map<String, Object> unwrapMap(Map<?, ?> dict) {
        assert dict != null : "empty map";
        if (dict instanceof Mapper) {
            dict = ((Mapper) dict).toMap();
        }
        Map<String, Object> result = new HashMap<>();
        String key;
        Object value;
        for (Map.Entry<?, ?> entry : dict.entrySet()) {
            // key = (String) entry.getKey();
            key = getString(entry.getKey());
            value = unwrap(entry.getValue());
            result.put(key, value);
        }
        return result;
    }

    @Override
    public List<?> unwrapList(List<?> array) {
        assert array != null : "empty list";
        List<Object> result = new ArrayList<>();
        for (Object item : array) {
            result.add(unwrap(item));
        }
        return result;
    }

}
