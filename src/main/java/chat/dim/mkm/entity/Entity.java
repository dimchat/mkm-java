package chat.dim.mkm.entity;

public class Entity {

    public final ID identifier;

    public IEntityDataSource dataSource;

    public Entity(Entity entity) {
        super();
        this.identifier = entity.identifier;
        this.dataSource = entity.dataSource;
    }

    public Entity(ID identifier) {
        super();
        this.identifier = identifier;
        this.dataSource = null;
    }

    public String toString() {
        return "<Entity|" + identifier + " (" + getNumber() + ") \"" + getName() + "\">";
    }

    public boolean equals(Entity entity) {
        return identifier.equals(entity.identifier);
    }

    public NetworkType getType() {
        return identifier.getType();
    }

    public long getNumber() {
        return identifier.getNumber();
    }

    public String getName() {
        String name;
        if (dataSource != null) {
            // get from data source
            name = dataSource.getName(this);
            if (name != null && name.length() > 0) {
                return name;
            }
        }
        // get from identifier
        name = identifier.name;
        if (name != null && name.length() > 0) {
            return name;
        }
        return identifier.address.toString();
    }

    public Meta getMeta() {
        // get from data source
        return dataSource == null ? null: dataSource.getMeta(this);
    }
}
