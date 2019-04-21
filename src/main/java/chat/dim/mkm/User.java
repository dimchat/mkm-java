package chat.dim.mkm;

import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.entity.ID;

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

    public List<ID> getContacts() {
        IUserDataSource dataSource = (IUserDataSource) this.dataSource;
        return dataSource.getContacts(this);
    }

    public boolean addContact(ID contact) {
        IUserDataSource dataSource = (IUserDataSource) this.dataSource;
        return dataSource.addContact(contact, this);
    }

    public boolean removeContact(ID contact) {
        IUserDataSource dataSource = (IUserDataSource) this.dataSource;
        return dataSource.removeContact(contact, this);
    }
}
