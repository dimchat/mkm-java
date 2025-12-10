/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Albert Moky
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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Dictionary implements Mapper {

    private final Map<String, Object> dictionary;

    protected Dictionary() {
        super();
        dictionary = new HashMap<>();
    }

    protected Dictionary(Map<String, Object> map) {
        super();
        if (map == null) {
            assert false : "cannot initialize with an empty map!";
            map = new HashMap<>();
        } else if (map instanceof Mapper) {
            map = ((Mapper) map).toMap();
        }
        dictionary = map;
    }

    @Override
    public String getString(String key, String defaultValue) {
        return Converter.getString(dictionary.get(key), defaultValue);
    }
    @Override
    public String getString(String key) {
        return Converter.getString(dictionary.get(key));
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return Converter.getBoolean(dictionary.get(key), defaultValue);
    }
    @Override
    public Boolean getBoolean(String key) {
        return Converter.getBoolean(dictionary.get(key));
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return Converter.getInteger(dictionary.get(key), defaultValue);
    }
    @Override
    public Integer getInteger(String key) {
        return Converter.getInteger(dictionary.get(key));
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return Converter.getLong(dictionary.get(key), defaultValue);
    }
    @Override
    public Long getLong(String key) {
        return Converter.getLong(dictionary.get(key));
    }

    @Override
    public Byte getByte(String key, Byte defaultValue) {
        return Converter.getByte(dictionary.get(key), defaultValue);
    }
    @Override
    public Byte getByte(String key) {
        return Converter.getByte(dictionary.get(key));
    }

    @Override
    public Short getShort(String key, Short defaultValue) {
        return Converter.getShort(dictionary.get(key), defaultValue);
    }
    @Override
    public Short getShort(String key) {
        return Converter.getShort(dictionary.get(key));
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return Converter.getFloat(dictionary.get(key), defaultValue);
    }
    @Override
    public Float getFloat(String key) {
        return Converter.getFloat(dictionary.get(key));
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return Converter.getDouble(dictionary.get(key), defaultValue);
    }
    @Override
    public Double getDouble(String key) {
        return Converter.getDouble(dictionary.get(key));
    }

    @Override
    public Date getDateTime(String key, Date defaultValue) {
        return Converter.getDateTime(dictionary.get(key), defaultValue);
    }
    @Override
    public Date getDateTime(String key) {
        return Converter.getDateTime(dictionary.get(key));
    }

    @Override
    public void setDateTime(String key, Date time) {
        if (time == null) {
            remove(key);
        } else {
            put(key, time.getTime() / 1000.0d);
        }
    }

    @Override
    public void setString(String key, Stringer stringer) {
        if (stringer == null) {
            remove(key);
        } else {
            put(key, stringer.toString());
        }
    }

    @Override
    public void setMap(String key, Mapper mapper) {
        if (mapper == null) {
            remove(key);
        } else {
            put(key, mapper.toMap());
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return dictionary;
    }

    @Override
    public Map<String, Object> copyMap(boolean deepCopy) {
        if (deepCopy) {
            return Copier.deepCopyMap(dictionary);
        } else {
            return Copier.copyMap(dictionary);
        }
    }

    @Override
    public String toString() {
        return dictionary.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return dictionary.isEmpty();
        } else if (other instanceof Mapper) {
            if (this == other) {
                // same object
                return true;
            }
            // compare inner map
            other = ((Mapper) other).toMap();
        }
        return other instanceof Map && Comparator.mapEquals(dictionary, (Map<?, ?>) other);
    }

    @Override
    public int hashCode() {
        return dictionary.hashCode();
    }

    @Override
    public int size() {
        return dictionary.size();
    }

    @Override
    public boolean isEmpty() {
        return dictionary.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return dictionary.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return dictionary.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return dictionary.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return dictionary.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return dictionary.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        dictionary.putAll(m);
    }

    @Override
    public void clear() {
        dictionary.clear();
    }

    @Override
    public Set<String> keySet() {
        return dictionary.keySet();
    }

    @Override
    public Collection<Object> values() {
        return dictionary.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return dictionary.entrySet();
    }
}
