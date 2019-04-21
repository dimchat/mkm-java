package chat.dim.mkm;

import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.IEntityDataSource;

import java.util.List;

public interface IGroupDataSource extends IEntityDataSource {

    public ID getFounder(Group group);

    public ID getOwner(Group group);

    public List<ID> getMembers(Group group);

    public boolean addMember(ID member, Group group);
    public boolean removeMember(ID member, Group group);
}
