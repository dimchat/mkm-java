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
package chat.dim.mkm;

import chat.dim.crypto.Dictionary;
import chat.dim.mkm.entity.ID;

import java.util.Map;

public class Profile extends Dictionary {

    public final ID identifier;

    public Profile(Map<String, Object> dictionary) {
        super(dictionary);
        this.identifier = ID.getInstance(dictionary.get("ID"));
    }

    public Profile(ID identifier) {
        super();
        this.identifier = identifier;
        dictionary.put("ID", identifier.toString());
    }

    @SuppressWarnings("unchecked")
    public static Profile getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Profile) {
            return (Profile) object;
        } else if (object instanceof Map) {
            return new Profile((Map<String, Object>) object);
        } else {
            throw new IllegalArgumentException("unknown meta:" + object);
        }
    }

    public String getName() {
        return (String) dictionary.get("name");
    }

    public void setName(String name) {
        dictionary.put("name", name);
    }

    public String getAvatar() {
        return (String) dictionary.get("avatar");
    }

    public void setAvatar(String avatar) {
        dictionary.put("avatar", avatar);
    }
}
