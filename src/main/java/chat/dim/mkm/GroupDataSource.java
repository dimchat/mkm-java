package chat.dim.mkm;

import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.EntityDataSource;

import java.util.List;

public interface GroupDataSource extends EntityDataSource {

    public ID getFounder(Group group);

    public ID getOwner(Group group);

    public List<Object> getMembers(Group group);
    public int getCountOfMembers(Group group);
    public ID getMemberAtIndex(int index, Group group);

}
