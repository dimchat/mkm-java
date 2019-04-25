package chat.dim.mkm;

import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.entity.ID;

import java.util.ArrayList;
import java.util.List;

public class User extends Account {

    public final PrivateKey privateKey;

    public User(User user) {
        super(user);
        this.privateKey = user.privateKey;
    }

    public User(ID identifier, PrivateKey privateKey) {
        super(identifier);
        this.privateKey = privateKey;
    }

    public User(ID identifier) {
        this(identifier, null);
    }

    public PrivateKey getPrivateKey() {
        if (privateKey != null) {
            return privateKey;
        }
        if (this.dataSource == null) {
            return null;
        }
        // get from data source
        IUserDataSource dataSource = (IUserDataSource) this.dataSource;
        return dataSource.getPrivateKey(this);
    }

    public List<ID> getContacts() {
        if (this.dataSource == null) {
            return null;
        }
        IUserDataSource dataSource = (IUserDataSource) this.dataSource;
        List<ID> contacts = dataSource.getContacts(this);
        if (contacts != null) {
            return contacts;
        }
        int count = dataSource.getCountOfContacts(this);
        if (count <= 0) {
            return null;
        }
        contacts = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            contacts.add(dataSource.getContactAtIndex(index, this));
        }
        return contacts;
    }
}
