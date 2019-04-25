package chat.dim.mkm;

import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.IEntityDataSource;

import java.util.List;

public interface IUserDataSource extends IEntityDataSource {

    public PrivateKey getPrivateKey(User user);

    public List<ID> getContacts(User user);
    public int getCountOfContacts(User user);
    public ID getContactAtIndex(int index, User user);
}
