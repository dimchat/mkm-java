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
package chat.dim.type;

public class ConstantString implements Stringer {

    private final String string;

    protected ConstantString(String str) {
        super();
        assert str != null : "cannot initialize with an empty string";
        string = str;
    }
    protected ConstantString(Stringer str) {
        super();
        assert str != null : "cannot initialize with an empty string";
        string = str.toString();
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return string.isEmpty();
        } else if (other instanceof Stringer) {
            if (this == other) {
                // same object
                return true;
            }
            // compare inner string
            other = other.toString();
        }
        return string.equals(other);
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

    @Override
    public int length() {
        return string.length();
    }

    @Override
    public boolean isEmpty() {
        return string.isEmpty();
    }

    @Override
    public char charAt(int index) {
        return string.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return string.subSequence(start, end);
    }

    @Override
    public int compareTo(String other) {
        if (other == null) {
            return string.compareTo("");
        }
        return string.compareTo(other);
    }

    @Override
    public int compareToIgnoreCase(String other) {
        if (other == null) {
            return string.compareToIgnoreCase("");
        }
        return string.compareToIgnoreCase(other);
    }

    @Override
    public int compareToIgnoreCase(Stringer other) {
        if (other == null) {
            return string.compareToIgnoreCase("");
        }
        return string.compareToIgnoreCase(other.toString());
    }

    @Override
    public boolean equalsIgnoreCase(String other) {
        return string.equalsIgnoreCase(other);
    }

    @Override
    public boolean equalsIgnoreCase(Stringer other) {
        if (other == null) {
            return string.isEmpty();
        }
        return string.equalsIgnoreCase(other.toString());
    }
}
