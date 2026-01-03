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
package chat.dim.data;

import java.util.List;
import java.util.Map;

import chat.dim.base.BaseWrapper;
import chat.dim.base.DataWrapper;

/**
 *  Data Wrap Interface
 */
public abstract class Wrapper {

    /**
     *  Get inner String
     *  <p>
     *      Remove first wrapper
     *  </p>
     */
    public static String getString(Object str) {
        return wrapper.getString(str);
    }

    /**
     *  Get inner Map
     *  <p>
     *      Remove first wrapper
     *  </p>
     */
    public static Map<String, Object> getMap(Object dict) {
        return wrapper.getMap(dict);
    }

    /**
     *  Unwrap recursively
     *  <p>
     *      Remove all wrappers
     *  </p>
     */
    public static Object unwrap(Object object) {
        return wrapper.unwrap(object);
    }

    // Unwrap values for keys in map
    public static Map<String, Object> unwrapMap(Map<?, ?> dict) {
        return wrapper.unwrapMap(dict);
    }
    // Unwrap values in the array
    public static List<?> unwrapList(List<?> array) {
        return wrapper.unwrapList(array);
    }

    /**
     *  Default Wrapper
     */
    public static DataWrapper wrapper = new BaseWrapper();

}
