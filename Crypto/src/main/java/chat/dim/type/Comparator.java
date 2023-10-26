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
package chat.dim.type;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface Comparator {

    static boolean identical(Object a, Object b) {
        if (a == null) {
            return b == null;
        } else if (b == null) {
            return false;
        } else {
            return a == b;
        }
    }

    static boolean different(Object a, Object b) {
        if (a == null) {
            return b != null;
        } else if (b == null) {
            return true;
        } else if (a == b) {
            // same object
            return false;
        } else if (a instanceof Map) {
            // check key-values
            if (b instanceof Map) {
                return !mapEquals((Map<?, ?>)a, (Map<?, ?>)b);
            }
            return true;
        } else if (a instanceof List) {
            // check elements
            if (b instanceof List) {
                return !listEquals((List<?>)a, (List<?>)b);
            }
            return true;
        } else {
            // other types
            return !a.equals(b);
        }
    }

    static boolean mapEquals(Map<?, ?> a, Map<?, ?> b) {
        if (a == null) {
            return b == null;
        } else if (b == null) {
            return false;
        } else if (a == b) {
            // same object
            return true;
        } else if (a.size() != b.size()) {
            // different lengths
            return false;
        }
        Iterator<? extends Map.Entry<?, ?>> iterator = a.entrySet().iterator();
        Map.Entry<?, ?> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            // check values
            if (different(entry.getValue(), b.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    static boolean listEquals(List<?> a, List<?> b) {
        if (a == null) {
            return b == null;
        } else if (b == null) {
            return false;
        } else if (a == b) {
            // same object
            return true;
        } else if (a.size() != b.size()) {
            // different lengths
            return false;
        }
        Iterator<?> ait = a.iterator();
        Iterator<?> bit = b.iterator();
        while (ait.hasNext() && bit.hasNext()) {
            // check elements
            if (different(ait.next(), bit.next())) {
                return false;
            }
        }
        return !(ait.hasNext() || bit.hasNext());
    }

}
