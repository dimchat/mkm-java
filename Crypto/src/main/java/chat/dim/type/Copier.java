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
import java.util.List;
import java.util.Map;

public interface Copier {

    /**
     *  Shallow Copy
     *  ~~~~~~~~~~~~
     */
    @SuppressWarnings("unchecked")
    static Object copy(Object object) {
        if (object instanceof Map) {
            return copyMap((Map<String, Object>) object);
        } else if (object instanceof List) {
            return copyList((List<Object>) object);
        //} else if (object instanceof Cloneable) {
        } else {
            return object;
        }
    }

    static Map<String, Object> copyMap(Map<String, Object> dict) {
        Map<String, Object> clone = new HashMap<>();
        for (Map.Entry<String, Object> entry : dict.entrySet()) {
            clone.put(entry.getKey(), entry.getValue());
        }
        return clone;
    }
    static List<Object> copyList(List<Object> array) {
        return new ArrayList<>(array);
    }

    /**
     *  Deep Copy
     *  ~~~~~~~~~
     */
    @SuppressWarnings("unchecked")
    static Object deepCopy(Object object) {
        if (object instanceof Map) {
            return deepCopyMap((Map<String, Object>) object);
        } else if (object instanceof List) {
            return deepCopyList((List<Object>) object);
        //} else if (object instanceof Cloneable) {
        } else {
            return object;
        }
    }

    static Map<String, Object> deepCopyMap(Map<String, Object> dict) {
        Map<String, Object> clone = new HashMap<>();
        for (Map.Entry<String, Object> entry : dict.entrySet()) {
            clone.put(entry.getKey(), deepCopy(entry.getValue()));
        }
        return clone;
    }
    static List<Object> deepCopyList(List<Object> array) {
        List<Object> clone = new ArrayList<>();
        for (Object item : array) {
            clone.add(deepCopy(item));
        }
        return clone;
    }
}
