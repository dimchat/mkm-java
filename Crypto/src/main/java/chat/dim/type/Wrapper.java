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

import java.lang.String;

public interface Wrapper {

    static Object unwrap(Object object, boolean circularly) {
        // unwrap string
        if (object instanceof chat.dim.type.String) {
            return object.toString();
        } else if (object instanceof String) {
            return object;
        }
        // unwrap container
        if (circularly) {
            // unwrap map circularly
            if (object instanceof Map) {
                return Map.unwrap(((Map) object).getMap(), true);
            } else if (object instanceof java.util.Map) {
                //noinspection unchecked
                return Map.unwrap((java.util.Map<String, Object>) object, true);
            }
            // unwrap list circularly
            if (object instanceof List) {
                return List.unwrap(((List) object).getList(), true);
            } else if (object instanceof java.util.List) {
                return List.unwrap((java.util.List) object, true);
            }
        } else {
            // unwrap map
            if (object instanceof Map) {
                return ((Map) object).getMap();
            }
            // unwrap list
            if (object instanceof List) {
                return ((List) object).getList();
            }
        }
        // others
        return object;
    }
}
