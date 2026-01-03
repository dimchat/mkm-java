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
package chat.dim.data;

import java.util.List;
import java.util.Map;

import chat.dim.base.BaseComparator;
import chat.dim.base.DataComparator;

/**
 *  Data Compare Interface
 */
public abstract class Comparator {

    public static boolean identical(Object a, Object b) {
        return comparator.identical(a, b);
    }

    public static boolean different(Object a, Object b) {
        return comparator.different(a, b);
    }

    public static boolean mapEquals(Map<?, ?> a, Map<?, ?> b) {
        return comparator.mapEquals(a, b);
    }

    public static boolean listEquals(List<?> a, List<?> b) {
        return comparator.listEquals(a, b);
    }

    /**
     *  Default Comparator
     */
    public static DataComparator comparator = new BaseComparator();

}
