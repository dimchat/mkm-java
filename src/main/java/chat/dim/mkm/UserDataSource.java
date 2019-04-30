package chat.dim.mkm;

import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.EntityDataSource;

import java.util.List;

public interface UserDataSource extends EntityDataSource {

    /**
     *  Get user private key
     *
     * @param user - user account
     * @return private key
     */
    public PrivateKey getPrivateKey(User user);

    /**
     *  Get contacts list
     *
     * @param user - user account
     * @return contacts list
     */
    public List<Object> getContacts(User user);

    /**
     *  Get contacts count
     *
     * @param user - user account
     * @return number of contacts
     */
    public int getCountOfContacts(User user);

    /**
     *  Get contact ID at index
     *
     * @param index - contact index
     * @param user - user account
     * @return contact ID
     */
    public ID getContactAtIndex(int index, User user);
}
