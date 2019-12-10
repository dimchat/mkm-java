/* license: https://mit-license.org
 *
 *  Ming-Ke-Ming : Decentralized User Identity Authentication
 *
 *                                Written in 2019 by Moky <albert.moky@gmail.com>
 *
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
package chat.dim;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.dim.crypto.*;
import chat.dim.format.JSON;
import chat.dim.impl.PrivateKeyImpl;

/**
 *  Built-in accounts (for test)
 *
 *      1. Immortal Hulk - hulk@4YeVEN3aUnvC1DNUufCq1bs9zoBSJTzVEj
 *      2. Monkey King   - moki@4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk
 */
public class Immortals implements UserDataSource {

    // Immortal Hulk
    public static final ID HULK = ID.getInstance("hulk@4YeVEN3aUnvC1DNUufCq1bs9zoBSJTzVEj");
    // Monkey King
    public static final ID MOKI = ID.getInstance("moki@4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk");

    // memory caches
    private Map<String, ID>     idMap         = new HashMap<>();
    private Map<ID, PrivateKey> privateKeyMap = new HashMap<>();
    private Map<ID, Meta>       metaMap       = new HashMap<>();
    private Map<ID, Profile>    profileMap    = new HashMap<>();
    private Map<ID, User>       userMap       = new HashMap<>();

    public Immortals() {
        super();
        try {
            loadBuiltInAccount(MOKI);
            loadBuiltInAccount(HULK);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadBuiltInAccount(ID identifier) throws IOException, ClassNotFoundException {
        assert identifier.isValid();
        idMap.put(identifier.toString(), identifier);
        // load meta for ID
        Meta meta = loadMeta("mkm/" + identifier.name + "_meta.js", identifier);
        assert meta != null;
        assert meta.matches(identifier);
        // load private key for ID
        PrivateKey key = loadPrivateKey("mkm/" + identifier.name + "_secret.js", identifier);
        assert key != null;
        // load profile for ID
        Profile profile = loadProfile("mkm/" + identifier.name + "_profile.js", identifier);
        assert profile != null;
    }

    private Meta loadMeta(String filename, ID identifier) throws IOException, ClassNotFoundException {
        Map dict = ResourceLoader.readJSONFile(filename);
        Meta meta = Meta.getInstance(dict);
        if (meta == null || !meta.matches(identifier)) {
            throw new NullPointerException("meta error: " + dict);
        }
        metaMap.put(identifier, meta);
        return meta;
    }

    private PrivateKey loadPrivateKey(String filename, ID identifier) throws IOException, ClassNotFoundException {
        Map dict = ResourceLoader.readJSONFile(filename);
        PrivateKey key = PrivateKeyImpl.getInstance(dict);
        if (key == null) {
            throw new NullPointerException("private key error: " + dict);
        }
        privateKeyMap.put(identifier, key);
        return key;
    }

    private Profile loadProfile(String filename, ID identifier) throws IOException {
        Map dict = ResourceLoader.readJSONFile(filename);
        Profile profile = Profile.getInstance(dict);
        if (profile == null || !identifier.equals(profile.getIdentifier())) {
            throw new NullPointerException("profile error: " + dict);
        }
        // copy 'name'
        Object name = dict.get("name");
        if (name == null) {
            Object names = dict.get("names");
            if (names instanceof List) {
                List array = (List) names;
                if (array.size() > 0) {
                    profile.setProperty("name", array.get(0));
                }
            }
        } else {
            assert name instanceof String;
            profile.setProperty("name", name);
        }
        // copy 'avatar'
        Object avatar = dict.get("avatar");
        if (avatar == null) {
            Object photos = dict.get("photos");
            if (photos instanceof List) {
                List array = (List) photos;
                if (array.size() > 0) {
                    profile.setProperty("avatar", array.get(0));
                }
            }
        } else {
            assert avatar instanceof String;
            profile.setProperty("avatar", avatar);
        }
        // sign and cache
        PrivateKey key = privateKeyMap.get(identifier);
        if (key == null) {
            throw new NullPointerException("failed to get private key for ID: " + identifier);
        }
        profile.sign(key);
        profileMap.put(identifier, profile);
        return profile;
    }

    //--------

    public ID getID(Object string) {
        if (string == null) {
            return null;
        } else if (string instanceof ID) {
            return (ID) string;
        }
        assert string instanceof String;
        return idMap.get(string);
    }

    public User getUser(ID identifier) {
        assert identifier.getType().isUser();
        User user = userMap.get(identifier);
        if (user == null) {
            if (idMap.containsValue(identifier)) {
                user = new User(identifier);
                user.setDataSource(this);
                userMap.put(identifier, user);
            }
        }
        return user;
    }

    //-------- EntityDataSource

    @Override
    public Meta getMeta(ID identifier) {
        return metaMap.get(identifier);
    }

    @Override
    public Profile getProfile(ID identifier) {
        return profileMap.get(identifier);
    }

    //-------- UserDataSource

    @Override
    public List<ID> getContacts(ID user) {
        if (!idMap.containsValue(user)) {
            return null;
        }
        List<ID> contacts = new ArrayList<>();
        for (ID value : idMap.values()) {
            if (value.equals(user)) {
                continue;
            }
            contacts.add(value);
        }
        return contacts;
    }

    @Override
    public EncryptKey getPublicKeyForEncryption(ID user) {
        // NOTICE: return nothing to use profile.key or meta.key
        return null;
    }

    @Override
    public List<DecryptKey> getPrivateKeysForDecryption(ID user) {
        PrivateKey key = privateKeyMap.get(user);
        if (key instanceof DecryptKey) {
            List<DecryptKey> array = new ArrayList<>();
            array.add((DecryptKey) key);
            return array;
        }
        return null;
    }

    @Override
    public SignKey getPrivateKeyForSignature(ID user) {
        return privateKeyMap.get(user);
    }

    @Override
    public List<VerifyKey> getPublicKeysForVerification(ID user) {
        // NOTICE: return nothing to use meta.key
        return null;
    }
}


class ResourceLoader {

    static Map readJSONFile(String filename) throws IOException {
        String json = readTextFile(filename);
        return (Map) JSON.decode(json);
    }

    private static String readTextFile(String filename) throws IOException {
        byte[] data = readResourceFile(filename);
        return new String(data, "UTF-8");
    }

    private static byte[] readResourceFile(String filename) throws IOException {
        InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(filename);
        assert is != null;
        int size = is.available();
        byte[] data = new byte[size];
        int len = is.read(data, 0, size);
        assert len == size;
        return data;
    }
}
