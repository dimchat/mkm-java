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

    // Unwrap keys, values circularly
    static Map<String, Object> unwrap(Map<String, Object> dictionary) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : dictionary.entrySet()) {
            result.put(entry.getKey(), unwrap(entry.getValue(), true));
        }
        return result;
    }

    // Unwrap items circularly
    static List<Object> unwrap(List<Object> array) {
        List<Object> result = new ArrayList<>();
        for (Object item : array) {
            result.add(unwrap(item, true));
        }
        return result;
    }

    // Remove Wrapper if exists
    @SuppressWarnings("unchecked")
    static Object unwrap(Object object, boolean circularly) {
        if (object == null) {
            return null;
        }
        // check for string
        if (object instanceof Stringer) {
            return object.toString();
        }
        // unwrap container
        if (circularly) {
            if (object instanceof List) {
                return unwrap((List<Object>) object);
            }
            if (object instanceof Mapper) {
                return unwrap(((Mapper) object).toMap());
            } else if (object instanceof Map) {
                return unwrap((Map<String, Object>) object);
            }
        } else if (object instanceof Mapper) {
            object = ((Mapper) object).toMap();
        }
        // OK
        return object;
    }
}
