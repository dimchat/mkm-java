package chat.dim.mkm;

import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.IEntityDataSource;

import java.util.List;

public interface IGroupDataSource extends IEntityDataSource {

    public ID getFounder(Group group);

    public ID getOwner(Group group);

    public List<ID> getMembers(Group group);
    public int getCountOfMembers(Group group);
    public ID getMemberAtIndex(int index, Group group);

}
