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

public abstract class String {

    private final java.lang.String string;

    protected String(java.lang.String str) {
        super();
        assert str != null : "cannot initialize with an empty string";
        string = str;
    }

    protected String(String string) {
        this(string.string);
    }

    public int length() {
        return string.length();
    }

    @Override
    public java.lang.String toString() {
        return string;
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            // same object
            return true;
        } else if (other instanceof String) {
            // check with inner string
            java.lang.String str = ((String) other).string;
            return string.equals(str);
        } else if (other instanceof java.lang.String) {
            // check string
            return string.equals(other);
        } else {
            // null or unknown object
            return false;
        }
    }

    public boolean equalsIgnoreCase(Object other) {
        if (super.equals(other)) {
            // same object
            return true;
        } else if (other instanceof String) {
            // check with inner string
            String str = (String) other;
            return string.equalsIgnoreCase(str.string);
        } else if (other instanceof java.lang.String) {
            // check string
            java.lang.String str = (java.lang.String) other;
            return string.equalsIgnoreCase(str);
        } else {
            // null or unknown object
            return false;
        }
    }
}
