import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.Account;
import chat.dim.mkm.Profile;
import chat.dim.mkm.User;
import chat.dim.mkm.UserDataSource;
import chat.dim.mkm.entity.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facebook implements MetaDataSource, EntityDataSource, UserDataSource {
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

    public void addUser(User user) {
        userMap.put(user.identifier.address, user);
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
    public Meta getMeta(Entity entity) {
        return getMeta(entity.identifier);
    }

    @Override
    public Profile getProfile(Entity entity) {
        return profileMap.get(entity.identifier.address);
    }

    @Override
    public String getName(Entity entity) {
        Profile profile = getProfile(entity);
        String name = profile.getName();
        if (name != null && name.length() > 0) {
            return name;
        }
        return entity.identifier.name;
    }

    //---- MetaDataSource

    @Override
    public Meta getMeta(ID identifier) {
        return metaMap.get(identifier.address);
    }
}
