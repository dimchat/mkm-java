import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.Account;
import chat.dim.mkm.GroupDataSource;
import chat.dim.mkm.User;
import chat.dim.mkm.UserDataSource;
import chat.dim.mkm.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facebook implements UserDataSource, GroupDataSource {

    private static Facebook ourInstance = new Facebook();

    public static Facebook getInstance() {
        return ourInstance;
    }

    private Facebook() {
    }

    // memory caches
    private Map<Address, PrivateKey> privateKeyMap = new HashMap<>();
    private Map<Address, Meta>       metaMap       = new HashMap<>();
    private Map<Address, Profile>    profileMap    = new HashMap<>();
    private Map<Address, Account>    accountMap    = new HashMap<>();
    private Map<Address, User>       userMap       = new HashMap<>();

    public EntityDataSource entityDataSource;
    public UserDataSource userDataSource;
    public GroupDataSource groupDataSource;

    public boolean cachePrivateKey(PrivateKey privateKey, ID identifier) {
        privateKeyMap.put(identifier.address, privateKey);
        return true;
    }

    public boolean cacheMeta(Meta meta, ID identifier) {
        metaMap.put(identifier.address, meta);
        return true;
    }

    public boolean cacheProfile(Profile profile) {
        profileMap.put(profile.identifier.address, profile);
        return true;
    }

    public boolean cacheAccount(Account account) {
        if (account instanceof User) {
            return cacheUser((User) account);
        }
        if (account.dataSource == null) {
            account.dataSource = this;
        }
        accountMap.put(account.identifier.address, account);
        return true;
    }

    public Account getAccount(ID identifier) {
        Account account = accountMap.get(identifier.address);
        if (account == null) {
            account = userMap.get(identifier.address);
            if (account == null) {
                account = new Account(identifier);
            }
        }
        if (account.dataSource == null) {
            account.dataSource = this;
        }
        return account;
    }

    public boolean cacheUser(User user) {
        if (user.dataSource == null) {
            user.dataSource = this;
        }
        userMap.put(user.identifier.address, user);
        return true;
    }

    public User getUser(ID identifier) {
        User user = userMap.get(identifier.address);
        if (user == null) {
            user = new User(identifier);
        }
        if (user.dataSource == null) {
            user.dataSource = this;
        }
        return user;
    }

    //---- UserDataSource

    @Override
    public PrivateKey getPrivateKeyForSignature(ID user) {
        PrivateKey key = privateKeyMap.get(user.address);
        if (key == null && userDataSource != null) {
            key = userDataSource.getPrivateKeyForSignature(user);
        }
        return key;
    }

    @Override
    public List<PrivateKey> getPrivateKeysForDecryption(ID user) {
        List<PrivateKey> list = userDataSource == null ? null : userDataSource.getPrivateKeysForDecryption(user);
        if (list != null && list.size() > 0) {
            privateKeyMap.put(user.address, list.get(0));
            return list;
        }
        PrivateKey key = privateKeyMap.get(user.address);
        if (key != null) {
            list = new ArrayList<>();
            list.add(key);
        }
        return list;
    }

    @Override
    public List<ID> getContacts(ID user) {
        return userDataSource == null ? null : userDataSource.getContacts(user);
    }

    //---- EntityDataSource

    @Override
    public boolean saveMeta(Meta meta, ID identifier) {
        return entityDataSource != null && entityDataSource.saveMeta(meta, identifier);
    }

    @Override
    public Meta getMeta(ID entity) {
        Meta meta = metaMap.get(entity.address);
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
            profile = profileMap.get(entity.address);
        } else {
            profileMap.put(entity.address, profile);
        }
        return profile;
    }

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
}
