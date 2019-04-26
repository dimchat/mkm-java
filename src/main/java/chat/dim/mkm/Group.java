package chat.dim.mkm;

import chat.dim.mkm.entity.Entity;
import chat.dim.mkm.entity.ID;

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

    public Group(ID identifier) {
        this(identifier, null);
    }

    public ID getFounder() {
        if (this.founder != null) {
            return this.founder;
        }
        if (this.dataSource == null) {
            return null;
        }
        // get from data source
        IGroupDataSource dataSource = (IGroupDataSource) this.dataSource;
        return dataSource.getFounder(this);
    }

    public ID getOwner() {
        if (this.dataSource == null) {
            return null;
        }
        // get from data source
        IGroupDataSource dataSource = (IGroupDataSource) this.dataSource;
        return dataSource.getOwner(this);
    }

    public List<ID> getMembers() {
        if (this.dataSource == null) {
            return null;
        }
        // get from data source
        IGroupDataSource dataSource = (IGroupDataSource) this.dataSource;
        List<ID> members = dataSource.getMembers(this);
        if (members != null) {
            return members;
        }
        int count = dataSource.getCountOfMembers(this);
        if (count <= 0) {
            return null;
        }
        // get members one by one
        members = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            members.add(dataSource.getMemberAtIndex(index, this));
        }
        return members;
    }
}
