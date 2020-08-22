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

import java.lang.String;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Dictionary<K, V> implements Map<K, V> {

    protected final Map<K, V> dictionary;

    protected Dictionary() {
        super();
        dictionary = new HashMap<>();
    }

    protected Dictionary(Map<K, V> map) {
        super();
        assert map != null : "cannot initialize with an empty map!";
        dictionary = map;
    }

    @SuppressWarnings("unchecked")
    protected static Object createInstance(Class clazz, Map dictionary) {
        // try 'Clazz.getInstance(dict)'
        try {
            Method method = clazz.getMethod("getInstance", Map.class);
            if (method.getDeclaringClass().equals(clazz)) {
                // only invoke the method 'getInstance' declared in this class
                return method.invoke(null, dictionary);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //e.printStackTrace();
        }
        // try 'new Clazz(dict)'
        try {
            Constructor constructor = clazz.getConstructor(Map.class);
            return constructor.newInstance(dictionary);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            // same object
            return true;
        } else if (other instanceof Map) {
            // check with inner dictionary
            return dictionary.equals(other);
        } else {
            // null or unknown object
            return false;
        }
    }

    @Override
    public String toString() {
        return dictionary.toString();
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
    public V get(Object key) {
        return dictionary.get(key);
    }

    @Override
    public V put(K key, V value) {
        return dictionary.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return dictionary.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        dictionary.putAll(m);
    }

    @Override
    public void clear() {
        dictionary.clear();
    }

    @Override
    public Set<K> keySet() {
        return dictionary.keySet();
    }

    @Override
    public Collection<V> values() {
        return dictionary.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return dictionary.entrySet();
    }
}
