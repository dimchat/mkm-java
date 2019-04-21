package chat.dim.mkm;

import chat.dim.mkm.entity.Entity;
import chat.dim.mkm.entity.ID;

import java.util.List;

public class Group extends Entity {

    public final ID founder;

    public Group(Group group) {
        super(group);
        this.founder = group.founder;
    }

    public Group(ID identifier, ID founder) {
        super(identifier);
        this.founder = founder;
    }

    public ID getOwner() {
        IGroupDataSource dataSource = (IGroupDataSource) this.dataSource;
        return dataSource.getOwner(this);
    }

    public List<ID> getMembers() {
        IGroupDataSource dataSource = (IGroupDataSource) this.dataSource;
        return dataSource.getMembers(this);
    }

    public boolean addMember(ID member) {
        IGroupDataSource dataSource = (IGroupDataSource) this.dataSource;
        return dataSource.addMember(member, this);
    }

    public boolean removeMember(ID member) {
        IGroupDataSource dataSource = (IGroupDataSource) this.dataSource;
        return dataSource.removeMember(member, this);
    }
}
