package chat.dim.mkm.entity;

/**
 *  enum MKMNetworkID
 *
 *  abstract A network type to indicate what kind the entity is.
 *
 *  discussion An address can identify a person, a group of people,
 *      a team, even a thing.
 *
 *      MKMNetwork_Main indicates this entity is a person's account.
 *      An account should have a public key, which proved by meta data.
 *
 *      MKMNetwork_Group indicates this entity is a group of people,
 *      which should have a founder (also the owner), and some members.
 *
 *      MKMNetwork_Moments indicates a special personal social network,
 *      where the owner can share informations and interact with its friends.
 *      The owner is the king here, it can do anything and no one can stop it.
 *
 *      MKMNetwork_Polylogue indicates a virtual (temporary) social network.
 *      It's created to talk with multi-people (but not too much, e.g. less than 100).
 *      Any member can invite people in, but only the founder can expel member.
 *
 *      MKMNetwork_Chatroom indicates a massive (persistent) social network.
 *      It's usually more than 100 people in it, so we need administrators
 *      to help the owner to manage the group.
 *
 *      MKMNetwork_SocialEntity indicates this entity is a social entity.
 *
 *      MKMNetwork_Organization indicates an independent organization.
 *
 *      MKMNetwork_Company indicates this entity is a company.
 *
 *      MKMNetwork_School indicates this entity is a school.
 *
 *      MKMNetwork_Government indicates this entity is a government department.
 *
 *      MKMNetwork_Department indicates this entity is a department.
 *
 *      MKMNetwork_Thing this is reserved for IoT (Internet of Things).
 *
 *  Bits:
 *      0000 0001 - this entity's branch is independent (clear division).
 *      0000 0010 - this entity can contains other group (big organization).
 *      0000 0100 - this entity is top organization.
 *      0000 1000 - (Main) this entity acts like a human.
 *
 *      0001 0000 - this entity contains members (Group)
 *      0010 0000 - this entity needs other administrators (big organization)
 *      0100 0000 - this is an entity in reality.
 *      1000 0000 - (IoT) this entity is a 'Thing'.
 *
 *      (All above are just some advices to help choosing numbers :P)
 */
public enum NetworkType {

    BTCMain        (0x00), // 0000 0000
    //BTCTest      (0x6f), // 0110 1111

    /**
     *  Person Account
     */
    Main           (0x08), // 0000 1000 (Person)

    /**
     *  Virtual Groups
     */
    Group          (0x10), // 0001 0000 (Multi-Persons)

    //Moments      (0x18), // 0001 1000 (Twitter)
    Polylogue      (0x10), // 0001 0000 (Multi-Persons Chat, N < 100)
    Chatroom       (0x30), // 0011 0000 (Multi-Persons Chat, N >= 100)

    /**
     *  Social Entities in Reality
     */
    //SocialEntity (0x50), // 0101 0000

    //Organization (0x74), // 0111 0100
    //Company      (0x76), // 0111 0110
    //School       (0x77), // 0111 0111
    //Government   (0x73), // 0111 0011
    //Department   (0x52), // 0101 0010

    /**
     *  Network
     */
    Provider       (0x76), // 0111 0110 (Service Provider)
    Station        (0x88), // 1000 1000 (Server Node)

    /**
     *  Internet of Things
     */
    Thing          (0x80), // 1000 0000 (IoT)
    Robot          (0xC8); // 1100 1000

    // Network ID
    public int value;

    NetworkType(int value) {
        this.value = value;
    }

    public byte toByte() {
        return (byte)this.value;
    }

    public static NetworkType fromByte(byte b) {
        NetworkType network = null;
        int i = b & 0xFF;
        switch (i) {
            case 0x00: {
                network = BTCMain;
                break;
            }
            case 0x08: {
                network = Main;
                break;
            }
            case 0x10: {
                network = Polylogue;
                break;
            }
            case 0x30: {
                network = Chatroom;
                break;
            }
            case 0x76: {
                network = Provider;
                break;
            }
            case 0x88: {
                network = Station;
                break;
            }
            case 0x80: {
                network = Thing;
                break;
            }
            case 0xC8: {
                network = Robot;
                break;
            }
            default: {
                break;
            }
        }
        return network;
    }

    public boolean isCommunicator() {
        return (this.value & Main.value) != 0 || this.value == BTCMain.value;
    }

    public boolean isPerson() {
        return this.value == Main.value || this.value == BTCMain.value;
    }

    public boolean isGroup() {
        return (this.value & Group.value) != 0;
    }

    public boolean isStation() {
        return this.value == Station.value;
    }

    public boolean isProvider() {
        return this.value == Provider.value;
    }

    public boolean isThing() {
        return (this.value & Thing.value) != 0;
    }

    public boolean isRobot() {
        return this.value == Robot.value;
    }
}
