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

import chat.dim.format.JSONList;

public interface List extends java.util.List {

    static java.util.List unwrap(java.util.List array, boolean circularly) {
        // unwrap list container
        if (array instanceof List) {
            array = ((List) array).getList();
        }
        if (!circularly) {
            return array;
        }
        // unwrap items circularly
        java.util.List<Object> result = new ArrayList<>();
        for (Object item : array) {
            result.add(Wrapper.unwrap(item, true));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    static java.util.List copyList(java.util.List array, boolean deepCopy) {
        if (deepCopy) {
            return JSONList.decode(JSONList.encode(array));
        } else {
            return new ArrayList(array);
        }
    }

    static java.util.List copyList(List array, boolean deepCopy) {
        return copyList(array.getList(), deepCopy);
    }

    java.util.List copyList(boolean deepCopy);

    java.util.List getList();
}
