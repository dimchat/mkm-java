package chat.dim.mkm.entity;

/**
 *  ID for entity (Account/Group)
 *
 *      data format: "name@address[/terminal]"
 *
 *      fileds:
 *          name     - entity name, the seed of fingerprint to build address
 *          address  - a string to identify an entity
 *          terminal - entity login resource(device), OPTIONAL
 */
public class ID {

    private final String string;

    public final String name;
    public final Address address;
    public final String terminal;

    public ID(String string) {
        this.string = string;
        // terminal
        String[] pair = string.split("/", 2);
        if (pair.length > 1) {
            this.terminal = pair[1];
        } else {
            this.terminal = null;
        }
        // name & address
        pair = pair[0].split("@", 2);
        if (pair.length > 1) {
            this.name = pair[0];
            this.address = new Address(pair[1]);
        } else {
            this.name = "";
            this.address = new Address(pair[0]);
        }
    }

    public ID(String name, Address address) {
        if (name == null || name.length() == 0) {
            // BTC address?
            this.string = address.toString();
        } else {
            this.string = name + "@" + address;
        }
        this.terminal = null;
        this.name = name;
        this.address = address;
    }

    /**
     *  For BTC address
     *
     * @param address - BTC address
     */
    public ID(Address address) {
        this(null, address);
    }

    public static ID getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof ID) {
            return (ID) object;
        } else if (object instanceof String) {
            return new ID((String) object);
        } else {
            throw new IllegalArgumentException("unknown ID:" + object);
        }
    }

    @Override
    public String toString() {
        return string;
    }

    public boolean equals(ID identifier) {
        if (name != null && !name.equals(identifier.name)) {
            return false;
        }
        return address.equals(identifier.address);
    }

    public boolean equals(String string) {
        return equals(new ID(string));
    }

    /**
     *  Get Network ID
     *
     * @return address type as network ID
     */
    public NetworkType getType() {
        return address.network;
    }

    /**
     *  Get Search Number
     *
     * @return number for searching this ID
     */
    public long getNumber() {
        return address.code;
    }

    public boolean isValid() {
        return address.valid;
    }
}
