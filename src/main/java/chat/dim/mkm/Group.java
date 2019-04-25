package chat.dim.mkm;

import chat.dim.mkm.entity.Entity;
import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.Meta;

import java.util.ArrayList;
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

    public boolean isFounder(ID identifier) {
        if (founder != null && founder.equals(identifier)) {
            return true;
        }
        Meta meta = getMeta();
        return meta != null && meta.matches(identifier);
    }

    public ID getFounder() {
        if (founder != null) {
            return founder;
        }
        // get from data source
        IGroupDataSource dataSource = (IGroupDataSource) this.dataSource;
        ID identifier = dataSource.getFounder(this);
        if (identifier != null) {
            return identifier;
        }
        // check each member
        List<ID> members = getMembers();
        for (ID member : members) {
            if (isFounder(member)) {
                return member;
            }
        }
        return null;
    }

    public ID getOwner() {
        IGroupDataSource dataSource = (IGroupDataSource) this.dataSource;
        return dataSource.getOwner(this);
    }

    public List<ID> getMembers() {
        IGroupDataSource dataSource = (IGroupDataSource) this.dataSource;
        List<ID> members = dataSource.getMembers(this);
        if (members != null) {
            return members;
        }
        int count = dataSource.getCountOfMembers(this);
        if (count <= 0) {
            return null;
        }
        members = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            members.add(dataSource.getMemberAtIndex(index, this));
        }
        return members;
    }
}
