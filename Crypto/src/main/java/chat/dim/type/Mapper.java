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
    Boolean getBoolean(String key, Boolean defaultValue);
    Integer getInteger(String key, Integer defaultValue);
    Long       getLong(String key, Long    defaultValue);
    Byte       getByte(String key, Byte    defaultValue);
    Short     getShort(String key, Short   defaultValue);
    Float     getFloat(String key, Float   defaultValue);
    Double   getDouble(String key, Double  defaultValue);

    String   getString(String key);
    Boolean getBoolean(String key);
    Integer getInteger(String key);
    Long       getLong(String key);
    Byte       getByte(String key);
    Short     getShort(String key);
    Float     getFloat(String key);
    Double   getDouble(String key);

    Date getDateTime(String key);
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
