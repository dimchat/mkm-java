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
    public PrivateKey getPrivateKey(int flag, User user) {
        return privateKeyMap.get(user.identifier.address);
    }

    @Override
    public List<Object> getContacts(User user) {
        return null;
    }

    @Override
    public int getCountOfContacts(User user) {
        return 0;
    }

    @Override
    public ID getContactAtIndex(int index, User user) {
        return null;
    }

    //---- EntityDataSource

    @Override
    public Meta getMeta(ID identifier) {
        return metaMap.get(identifier.address);
    }

    @Override
    public Profile getProfile(ID identifier) {
        return profileMap.get(identifier.address);
    }
}
