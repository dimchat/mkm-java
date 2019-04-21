package chat.dim.mkm.entity;

public class Entity {

    public final ID identifier;

    public IEntityDataSource dataSource;

    public Entity(Entity entity) {
        this(entity.identifier);
        this.dataSource = entity.dataSource;
    }

    public Entity(ID identifier) {
        this.identifier = identifier;
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
            // get name via delegate
            name = dataSource.getName(this);
            if (name != null && name.length() > 0) {
                return name;
            }
        }
        name = identifier.name;
        if (name != null && name.length() > 0) {
            return name;
        }
        return identifier.address.toString();
    }

    public Meta getMeta() {
        return dataSource.getMeta(this);
    }

    public static void main(String args[]) {
        ID identifier = new ID("moky@4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
        Entity entity = new Entity(identifier);
        System.out.println(entity);
    }
}
