import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.*;
import chat.dim.mkm.entity.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facebook implements UserDataSource {
    private static Facebook ourInstance = new Facebook();

    public static Facebook getInstance() {
        return ourInstance;
    }

    private Facebook() {
    }

    // memory caches
    private Map<Address, Meta>       metaMap       = new HashMap<>();
    private Map<Address, Account>    accountMap    = new HashMap<>();
    private Map<Address, User>       userMap       = new HashMap<>();
    private Map<Address, PrivateKey> privateKeyMap = new HashMap<>();
    private Map<Address, Profile>    profileMap    = new HashMap<>();

    public void addMeta(Meta meta, ID identifier) {
        metaMap.put(identifier.address, meta);
    }

    public void addAccount(Account account) {
        if (account instanceof User) {
            addUser((User) account);
            return;
        }
        if (account.dataSource == null) {
            account.dataSource = this;
        }
        accountMap.put(account.identifier.address, account);
    }

    public Account getAccount(ID identifier) {
        Account account = accountMap.get(identifier.address);
        if (account == null) {
            account = userMap.get(identifier.address);
        }
        return account;
    }

    public void addUser(User user) {
        if (user.dataSource == null) {
            user.dataSource = this;
        }
        userMap.put(user.identifier.address, user);
    }

    public User getUser(ID identifier) {
        return userMap.get(identifier.address);
    }

    public void addPrivateKey(PrivateKey privateKey, ID identifier) {
        privateKeyMap.put(identifier.address, privateKey);
    }

    public void addProfile(Profile profile) {
        profileMap.put(profile.identifier.address, profile);
    }

    //---- UserDataSource

    @Override
    public PrivateKey getPrivateKey(int flag, ID user) {
        return privateKeyMap.get(user.address);
    }

    @Override
    public List<ID> getContacts(ID user) {
        return null;
    }

    @Override
    public int getCountOfContacts(ID user) {
        return 0;
    }

    @Override
    public ID getContactAtIndex(int index, ID user) {
        return null;
    }

    //---- EntityDataSource

    @Override
    public Meta getMeta(ID entity) {
        return metaMap.get(entity.address);
    }

    @Override
    public Profile getProfile(ID entity) {
        return profileMap.get(entity.address);
    }
}
