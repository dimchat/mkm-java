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

public class BaseConverter implements DataConverter {

    @Override
    public String getString(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof String) {
            // exactly
            return (String) value;
        } else {
            //assert false : "not a string value: " + value;
            return value.toString();
        }
    }

    private String getString(Object value) {
        return value instanceof String ? (String) value : value.toString();
    }

    @Override
    public Boolean getBoolean(Object value, Boolean defaultValue) throws NumberFormatException {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Boolean) {
            // exactly
            return (Boolean) value;
        } else if (value instanceof Number) {  // Byte, Short, Integer, Long, Float, Double
            Number number = (Number) value;
            if (value instanceof Float || value instanceof Double) {
                double d = number.doubleValue();
                assert d == 1.0 || d == 0.0 : "Boolean value error: " + value;
                return d != 0.0;
            }
            long i = number.longValue();
            assert i == 1 || i == 0 : "Boolean value error: " + value;
            return i != 0;
        }
        String str = getString(value);
        str = str.trim();
        if (str.isEmpty()) {
            return false;
        } else if (str.length() > Converter.MAX_BOOLEAN_LEN) {
            throw new NumberFormatException("Boolean value error: \"" + value + "\"");
        }
        str = str.toLowerCase();
        Boolean state = Converter.BOOLEAN_STATES.get(str);
        if (state == null) {
            throw new NumberFormatException("Boolean value error: \"" + value + "\"");
        }
        return state;
    }

    @Override
    public Byte getByte(Object value, Byte defaultValue) throws NumberFormatException {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Byte) {
            // exactly
            return (Byte) value;
        } else if (value instanceof Number) {  // Short, Integer, Long, Float, Double
            return ((Number) value).byteValue();
        } else if (value instanceof Boolean) {
            return (byte) ((Boolean) value ? 1 : 0);
        }
        String str = getString(value);
        return Byte.parseByte(str);
    }

    @Override
    public Short getShort(Object value, Short defaultValue) throws NumberFormatException {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Short) {
            // exactly
            return (Short) value;
        } else if (value instanceof Number) {  // Byte, Integer, Long, Float, Double
            return ((Number) value).shortValue();
        } else if (value instanceof Boolean) {
            return (short) ((Boolean) value ? 1 : 0);
        }
        String str = getString(value);
        return Short.parseShort(str);
    }

    @Override
    public Integer getInteger(Object value, Integer defaultValue) throws NumberFormatException {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Integer) {
            // exactly
            return (Integer) value;
        } else if (value instanceof Number) {  // Byte, Short, Long, Float, Double
            return ((Number) value).intValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        String str = getString(value);
        return Integer.parseInt(str);
    }

    @Override
    public Long getLong(Object value, Long defaultValue) throws NumberFormatException {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Long) {
            // exactly
            return (Long) value;
        } else if (value instanceof Number) {  // Byte, Short, Integer, Float, Double
            return ((Number) value).longValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1L : 0L;
        }
        String str = getString(value);
        return Long.parseLong(str);
    }

    @Override
    public Float getFloat(Object value, Float defaultValue) throws NumberFormatException {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Float) {
            // exactly
            return (Float) value;
        } else if (value instanceof Number) {  // Byte, Short, Integer, Long, Double
            return ((Number) value).floatValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1.0F : 0.0F;
        }
        String str = getString(value);
        return Float.parseFloat(str);
    }

    @Override
    public Double getDouble(Object value, Double defaultValue) throws NumberFormatException {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Double) {
            // exactly
            return (Double) value;
        } else if (value instanceof Number) {  // Byte, Short, Integer, Long, Float
            return ((Number) value).doubleValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1.0 : 0.0;
        }
        String str = getString(value);
        return Double.parseDouble(str);
    }

    @Override
    public Date getDateTime(Object value, Date defaultValue) throws NumberFormatException {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Date) {
            // exactly
            return (Date) value;
        }
        Double seconds = getDouble(value, null);
        if (seconds == null || seconds < 0) {
            throw new NumberFormatException("Timestamp error: \"" + value + "\"");
        }
        double millis = seconds * 1000;
        return new Date((long) millis);
    }

}
