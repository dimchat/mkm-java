package chat.dim.mkm.entity;

public class Entity {

    public final ID identifier;

    public EntityDataSource dataSource;

    public Entity(ID identifier) {
        super();
        this.identifier = identifier;
        this.dataSource = null;
    }

    @Override
    public String toString() {
        String clazzName = getClass().getSimpleName();
        return "<" + clazzName + "|" + getType() + " " + identifier + " (" + getNumber() + ") \"" + getName() + "\">";
    }

    public boolean equals(Entity entity) {
        return identifier.equals(entity.identifier);
    }

    /**
     *  Get entity type
     *
     * @return type
     */
    public NetworkType getType() {
        return identifier.getType();
    }

    /**
     *  Get Search Number
     *
     * @return number for searching this entity
     */
    public long getNumber() {
        return identifier.getNumber();
    }

    /**
     *  Get entity name
     *
     * @return name string
     */
    public String getName() {
        String name;
        // get from data source
        if (dataSource != null) {
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
