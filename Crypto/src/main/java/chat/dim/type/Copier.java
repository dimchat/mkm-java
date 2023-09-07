/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Albert Moky
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface Copier {

    /**
     *  Shallow Copy
     *  ~~~~~~~~~~~~
     */
    static Object copy(Object object) {
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

    static Map<String, Object> copyMap(Map<?, ?> dict) {
        Map<String, Object> clone = new HashMap<>();
        Iterator<? extends Map.Entry<?, ?>> iterator = dict.entrySet().iterator();
        Map.Entry<?, ?> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            clone.put(Wrapper.getString(entry.getKey()), entry.getValue());
        }
        return clone;
    }
    static List<Object> copyList(List<?> array) {
        return new ArrayList<>(array);
    }

    /**
     *  Deep Copy
     *  ~~~~~~~~~
     */
    static Object deepCopy(Object object) {
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

    static Map<String, Object> deepCopyMap(Map<?, ?> dict) {
        Map<String, Object> clone = new HashMap<>();
        Iterator<? extends Map.Entry<?, ?>> iterator = dict.entrySet().iterator();
        Map.Entry<?, ?> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            clone.put(Wrapper.getString(entry.getKey()), deepCopy(entry.getValue()));
        }
        return clone;
    }
    static List<Object> deepCopyList(List<?> array) {
        List<Object> clone = new ArrayList<>();
        for (Object item : array) {
            clone.add(deepCopy(item));
        }
        return clone;
    }
}
