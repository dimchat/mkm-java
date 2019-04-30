package chat.dim.mkm;

import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.EntityDataSource;

import java.util.List;

public interface GroupDataSource extends EntityDataSource {

    /**
     *  Get group founder
     *
     * @param group - group object
     * @return fonder ID
     */
    public ID getFounder(Group group);

    /**
     *  Get group owner
     *
     * @param group - group object
     * @return owner ID
     */
    public ID getOwner(Group group);

    /**
     *  Get group members list
     *
     * @param group - group objet
     * @return members list
     */
    public List<Object> getMembers(Group group);

    /**
     *  Get members count
     *
     * @param group - group object
     * @return number of members
     */
    public int getCountOfMembers(Group group);

    /**
     *  Get member ID at index
     *
     * @param index - member index
     * @param group - group object
     * @return member id
     */
    public ID getMemberAtIndex(int index, Group group);

}
