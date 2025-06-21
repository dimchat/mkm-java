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

import java.util.Date;
import java.util.Map;

public interface Mapper extends Map<String, Object> {

    String   getString(String key, String  defaultValue);
    boolean getBoolean(String key, boolean defaultValue);
    int         getInt(String key, int     defaultValue);
    long       getLong(String key, long    defaultValue);
    byte       getByte(String key, byte    defaultValue);
    short     getShort(String key, short   defaultValue);
    float     getFloat(String key, float   defaultValue);
    double   getDouble(String key, double  defaultValue);

    Date getDateTime(String key, Date defaultValue);
    void setDateTime(String key, Date time);

    void setString(String key, Stringer stringer);
    void setMap(String key, Mapper mapper);

    /**
     *  Get inner map
     *
     * @return Map
     */
    Map<String, Object> toMap();

    /**
     *  Copy inner map
     *
     * @param deepCopy
     *        deep copy
     *
     * @return Map
     */
    Map<String, Object> copyMap(boolean deepCopy);
}
