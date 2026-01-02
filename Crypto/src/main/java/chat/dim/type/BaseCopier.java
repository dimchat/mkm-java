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

public class BaseCopier implements DataCopier {

    @Override
    public Object copy(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Mapper) {
            return copyMap(((Mapper) object).toMap());
        } else if (object instanceof Map) {
            return copyMap((Map<?, ?>) object);
        } else if (object instanceof List) {
            return copyList((List<?>) object);
        //} else if (object instanceof Cloneable) {
        } else {
            return object;
        }
    }

    @Override
    public Map<String, Object> copyMap(Map<?, ?> dict) {
        Map<String, Object> clone = new HashMap<>();
        String key;
        Object value;
        for (Map.Entry<?, ?> entry : dict.entrySet()) {
            key = Wrapper.getString(entry.getKey());
            value = entry.getValue(); // copy(entry.getValue());
            clone.put(key, value);
        }
        return clone;
    }

    @Override
    public <V> List<V> copyList(List<V> array) {
        return new ArrayList<>(array);
    }

    @Override
    public Object deepCopy(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Mapper) {
            return deepCopyMap(((Mapper) object).toMap());
        } else if (object instanceof Map) {
            return deepCopyMap((Map<?, ?>) object);
        } else if (object instanceof List) {
            return deepCopyList((List<?>) object);
        //} else if (object instanceof Cloneable) {
        } else {
            return object;
        }
    }

    @Override
    public Map<String, Object> deepCopyMap(Map<?, ?> dict) {
        Map<String, Object> clone = new HashMap<>();
        String key;
        Object value;
        for (Map.Entry<?, ?> entry : dict.entrySet()) {
            key = Wrapper.getString(entry.getKey());
            value = deepCopy(entry.getValue());
            clone.put(key, value);
        }
        return clone;
    }

    @Override
    public List<Object> deepCopyList(List<?> array) {
        List<Object> clone = new ArrayList<>();
        for (Object item : array) {
            clone.add(deepCopy(item));
        }
        return clone;
    }

}
