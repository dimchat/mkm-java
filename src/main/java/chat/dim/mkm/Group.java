package chat.dim.mkm;

import chat.dim.mkm.entity.Entity;
import chat.dim.mkm.entity.ID;

import java.util.ArrayList;
import java.util.List;

public class Group extends Entity {

    private final ID founder;

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
        GroupDataSource dataSource = (GroupDataSource) this.dataSource;
        return dataSource.getFounder(this);
    }

    public ID getOwner() {
        if (this.dataSource == null) {
            return null;
        }
        // get from data source
        GroupDataSource dataSource = (GroupDataSource) this.dataSource;
        return dataSource.getOwner(this);
    }

    public List<Object> getMembers() {
        if (this.dataSource == null) {
            return null;
        }
        // get from data source
        GroupDataSource dataSource = (GroupDataSource) this.dataSource;
        List<Object> members = dataSource.getMembers(this);
        if (members != null) {
            return members;
        }
        int count = dataSource.getCountOfMembers(this);
        if (count == 0) {
            return null;
        } else if (count < 0) {
            throw new ArrayIndexOutOfBoundsException("failed to get members of group:" + identifier);
        }
        // get members one by one
        members = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            members.add(dataSource.getMemberAtIndex(index, this));
        }
        return members;
    }
}
