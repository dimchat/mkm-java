
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
import chat.dim.crypto.impl.PrivateKeyImpl;
import chat.dim.format.Base64;
import chat.dim.format.JSON;
import chat.dim.mkm.*;

public class Facebook implements UserDataSource, GroupDataSource {

    private static Facebook ourInstance = new Facebook();

    public static Facebook getInstance() {
        return ourInstance;
    }

    private Facebook() {
    }

    // memory caches
    private Map<Address, PrivateKey> privateKeyMap = new HashMap<>();
    private Map<ID, Meta>            metaMap       = new HashMap<>();
    private Map<ID, Profile>         profileMap    = new HashMap<>();
    private Map<ID, User>            userMap       = new HashMap<>();

    public EntityDataSource entityDataSource;
    public UserDataSource userDataSource;
    public GroupDataSource groupDataSource;

    public boolean cachePrivateKey(PrivateKey privateKey, ID identifier) {
        privateKeyMap.put(identifier.address, privateKey);
        return true;
    }

    public boolean cacheMeta(Meta meta, ID identifier) {
        metaMap.put(identifier, meta);
        return true;
    }

    public boolean cacheProfile(Profile profile) {
        ID identifier = ID.getInstance(profile.getIdentifier());
        profileMap.put(identifier, profile);
        return true;
    }

    public boolean cacheUser(User user) {
        if (user.getDataSource() == null) {
            user.setDataSource(this);
        }
        userMap.put(user.identifier, user);
        return true;
    }

    public User getUser(ID identifier) {
        User user = userMap.get(identifier);
        if (user != null) {
            return user;
        }
        user = new User(identifier);
        user.setDataSource(this);
        userMap.put(identifier, user);
        return user;
    }

    //-------- EntityDataSource

    @Override
    public Meta getMeta(ID entity) {
        Meta meta = metaMap.get(entity);
        if (meta != null) {
            return meta;
        }
        if (entityDataSource == null) {
            return null;
        }
        meta = entityDataSource.getMeta(entity);
        if (meta != null) {
            cacheMeta(meta, entity);
        }
        return meta;
    }

    @Override
    public Profile getProfile(ID entity) {
        Profile profile = entityDataSource == null ? null : entityDataSource.getProfile(entity);
        if (profile == null) {
            profile = profileMap.get(entity);
        } else {
            profileMap.put(entity, profile);
        }
        return profile;
    }

    //------- UserDataSource

    @Override
    public List<ID> getContacts(ID user) {
        return userDataSource == null ? null : userDataSource.getContacts(user);
    }

    @Override
    public PrivateKey getPrivateKeyForSignature(ID user) {
        PrivateKey key;
        if (userDataSource != null) {
            key = userDataSource.getPrivateKeyForSignature(user);
            if (key != null) {
                privateKeyMap.put(user.address, key);
            }
        }
        return privateKeyMap.get(user.address);
    }

    @Override
    public List<PublicKey> getPublicKeysForVerification(ID user) {
        return null;
    }

    @Override
    public PublicKey getPublicKeyForEncryption(ID user) {
        return null;
    }

    @Override
    public List<PrivateKey> getPrivateKeysForDecryption(ID user) {
        List<PrivateKey> list;
        if (userDataSource != null) {
            list = userDataSource.getPrivateKeysForDecryption(user);
            if (list != null && list.size() > 0) {
                privateKeyMap.put(user.address, list.get(0));
                return list;
            }
        }
        list = new ArrayList<>();
        PrivateKey key = privateKeyMap.get(user.address);
        if (key != null) {
            list.add(key);
        }
        return list;
    }

    //-------- GroupDataSource

    @Override
    public ID getFounder(ID group) {
        return null;
    }

    @Override
    public ID getOwner(ID group) {
        return null;
    }

    @Override
    public List<ID> getMembers(ID group) {
        return null;
    }

    //-------- load immortals

    @SuppressWarnings("unchecked")
    private static Profile getProfile(Map dictionary, ID identifier, PrivateKey privateKey) {
        Profile profile;
        String profile_data = (String) dictionary.get("data");
        if (profile_data == null) {
            profile = new Profile(identifier);
            // set name
            String name = (String) dictionary.get("name");
            if (name == null) {
                List<String> names = (List<String>) dictionary.get("names");
                if (names != null) {
                    if (names.size() > 0) {
                        name = names.get(0);
                    }
                }
            }
            profile.setName(name);
            for (Object key : dictionary.keySet()) {
                if (key.equals("ID")) {
                    continue;
                }
                if (key.equals("name") || key.equals("names")) {
                    continue;
                }
                profile.setProperty((String) key, dictionary.get(key));
            }
            // sign profile
            profile.sign(privateKey);
        } else {
            String signature = (String) dictionary.get("signature");
            if (signature == null) {
                profile = new Profile(identifier, profile_data, null);
                // sign profile
                profile.sign(privateKey);
            } else {
                profile = new Profile(identifier, profile_data, Base64.decode(signature));
                // verify
                profile.verify(privateKey.getPublicKey());
            }
        }
        return profile;
    }

    static User loadBuiltInAccount(String filename) throws IOException, ClassNotFoundException {
        String jsonString = Utils.readTextFile(filename);
        Map dictionary = (Map) JSON.decode(jsonString);

        // ID
        ID identifier = ID.getInstance(dictionary.get("ID"));
        assert identifier != null;
        // meta
        Meta meta = Meta.getInstance(dictionary.get("meta"));
        assert meta != null && meta.matches(identifier);
        getInstance().cacheMeta(meta, identifier);
        // private key
        PrivateKey privateKey = PrivateKeyImpl.getInstance(dictionary.get("privateKey"));
        if (meta.key.matches(privateKey)) {
            // store private key into keychain
            getInstance().cachePrivateKey(privateKey, identifier);
        } else {
            throw new IllegalArgumentException("private key not match meta public key: " + privateKey);
        }
        // create user
        User user = new User(identifier);
        getInstance().cacheUser(user);

        // profile
        Profile profile = getProfile((Map) dictionary.get("profile"), identifier, privateKey);
        getInstance().cacheProfile(profile);

        return user;
    }

    static {
        try {
            loadBuiltInAccount("/mkm_hulk.js");
            loadBuiltInAccount("/mkm_moki.js");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
