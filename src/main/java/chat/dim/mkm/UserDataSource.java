package chat.dim.mkm;

import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.EntityDataSource;

import java.util.List;

public interface UserDataSource extends EntityDataSource {

    public PrivateKey getPrivateKey(User user);

    public List<Object> getContacts(User user);
    public int getCountOfContacts(User user);
    public ID getContactAtIndex(int index, User user);
}
