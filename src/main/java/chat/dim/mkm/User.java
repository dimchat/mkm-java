package chat.dim.mkm;

import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.entity.ID;

import java.util.ArrayList;
import java.util.List;

public class User extends Account {

    private final PrivateKey privateKey;

    public User(ID identifier, PrivateKey privateKey) {
        super(identifier, privateKey == null ? null : privateKey.getPublicKey());
        this.privateKey = privateKey;
    }

    public User(ID identifier) {
        this(identifier, null);
    }

    public PrivateKey getPrivateKey() {
        if (this.privateKey != null) {
            return this.privateKey;
        }
        if (this.dataSource == null) {
            return null;
        }
        // get from data source
        UserDataSource dataSource = (UserDataSource) this.dataSource;
        return dataSource.getPrivateKey(this);
    }

    public List<Object> getContacts() {
        if (this.dataSource == null) {
            return null;
        }
        UserDataSource dataSource = (UserDataSource) this.dataSource;
        List<Object> contacts = dataSource.getContacts(this);
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
