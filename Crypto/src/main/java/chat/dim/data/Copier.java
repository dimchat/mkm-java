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
package chat.dim.data;

import java.util.List;
import java.util.Map;

import chat.dim.base.BaseCopier;
import chat.dim.base.DataCopier;

/**
 *  Data Copy Interface
 */
public abstract class Copier {

    /**
     *  Shallow Copy
     */
    public static Object copy(Object object) {
        return copier.copy(object);
    }

    public static Map<String, Object> copyMap(Map<?, ?> dict) {
        return copier.copyMap(dict);
    }

    public static <V> List<V> copyList(List<V> array) {
        return copier.copyList(array);
    }

    /**
     *  Deep Copy
     */
    public static Object deepCopy(Object object) {
        return copier.deepCopy(object);
    }

    public static Map<String, Object> deepCopyMap(Map<?, ?> dict) {
        return copier.deepCopyMap(dict);
    }

    public static List<Object> deepCopyList(List<?> array) {
        return copier.deepCopyList(array);
    }

    /**
     *  Default Copier
     */
    public static DataCopier copier = new BaseCopier();

}
