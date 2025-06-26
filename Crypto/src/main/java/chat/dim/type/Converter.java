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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class Converter {

    public static Map<String, Boolean> BOOLEAN_STATES = new HashMap<String, Boolean>() {{
        put("1", true); put("yes", true); put("true", true); put("on", true);

        put("0", false); put("no", false); put("false", false); put("off", false);
        put("+0", false); put("-0", false); put("+0.0", false); put("-0.0", false);
        put("none", false); put("null", false); put("undefined", false);
    }};
    public static int MAX_BOOLEAN_LEN = "undefined".length();

    public static String getString(Object value, String defaultValue) {
        return converter.getString(value, defaultValue);
    }

    /**
     *  Assume value can be a config string:
     *  <blockquote><pre>
     *      'true', 'false', 'yes', 'no', 'on', 'off', '1', '0', ...
     *  </pre></blockquote>
     */
    public static Boolean getBoolean(Object value, Boolean defaultValue) {
        return converter.getBoolean(value, defaultValue);
    }

    public static Byte getByte(Object value, Byte defaultValue) {
        return converter.getByte(value, defaultValue);
    }
    public static Short getShort(Object value, Short defaultValue) {
        return converter.getShort(value, defaultValue);
    }

    public static Integer getInteger(Object value, Integer defaultValue) {
        return converter.getInteger(value, defaultValue);
    }
    public static Long getLong(Object value, Long defaultValue) {
        return converter.getLong(value, defaultValue);
    }

    public static Float getFloat(Object value, Float defaultValue) {
        return converter.getFloat(value, defaultValue);
    }
    public static Double getDouble(Object value, Double defaultValue) {
        return converter.getDouble(value, defaultValue);
    }

    /**
     *  Assume value can be a timestamp:
     *        (seconds from 1970-01-01 00:00:00)
     */
    public static Date getDateTime(Object value, Date defaultValue) {
        return converter.getDateTime(value, defaultValue);
    }

    public static DataConverter converter = new DataConverter();

}
