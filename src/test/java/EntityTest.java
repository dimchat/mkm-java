import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
import chat.dim.crypto.impl.PrivateKeyImpl;
import chat.dim.mkm.Account;
import chat.dim.mkm.User;
import chat.dim.mkm.entity.Address;
import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.Meta;
import chat.dim.mkm.entity.NetworkType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTest {

    static String satoshi = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa";

    private String getAddressInfo(Address address) {
        Map<String, Object> info = new HashMap<>();
        info.put("type", address.getCode());
        info.put("number", address.getNetwork());
        return info.toString();
    }

    private String getIDInfo(ID identifier) {
        Map<String, Object> info = new HashMap<>();
        info.put("type", identifier.getType());
        info.put("number", identifier.getNumber());
        info.put("valid", identifier.isValid());
        info.put("name", identifier.name);
        info.put("address", identifier.address);
        info.put("terminal", identifier.terminal);
        return info.toString();
    }

    private String getMetaInfo(Meta meta) {
        return meta.toString();
    }

    @Test
    public void testAddress() {
        Address address;

        address = Address.getInstance("4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk");
        Log.info("address: " + address + ", detail: " + getAddressInfo(address));

        address = Address.getInstance("4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
        Log.info("address: " + address + ", detail: " + getAddressInfo(address));

        NetworkType robot = NetworkType.Robot;
        Log.info("robot type: " + robot.toByte());
        Assert.assertEquals((byte) 0xC8, robot.toByte());

        address = Address.getInstance(satoshi);
        Log.info("satoshi: " + address);
    }

    @Test
    public void testID() {
        ID identifier;

        identifier = ID.getInstance("moki@4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk");
        Log.info("ID: " + identifier + ", detail: " + getIDInfo(identifier));
        Assert.assertEquals(1840839527L, identifier.getNumber());

        identifier = ID.getInstance("moky@4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
        Log.info("ID: " + identifier + ", detail: " + getIDInfo(identifier));
        Assert.assertEquals(4049699527L, identifier.getNumber());

        Assert.assertEquals(identifier, ID.getInstance("moky@4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ/home"));

        List<ID> array = new ArrayList<>();
        array.add(identifier);
        array.add(identifier);
        Log.info("list<ID>: " + array);

        Assert.assertTrue(identifier.isValid());
    }

    @Test
    public void testMeta() throws ClassNotFoundException {
        PrivateKey sk = PrivateKeyImpl.generate(PrivateKey.RSA);
        PublicKey pk = sk.getPublicKey();
        String seed = "moky";
        byte[] data = seed.getBytes(Charset.forName("UTF-8"));
        Meta meta = Meta.generate(Meta.VersionDefault, sk, seed);
        Log.info("meta: " + meta + ", detail: " + getMetaInfo(meta));
        Assert.assertTrue(meta.matches(pk));

        ID identifier = meta.generateID(NetworkType.Main);
        Log.info("ID: " + identifier + ", detail: " + getIDInfo(identifier));
        Assert.assertTrue(meta.matches(identifier));
        Assert.assertTrue(meta.matches(identifier.address));

        Assert.assertTrue(identifier.getType().isCommunicator());
        Assert.assertTrue(identifier.getType().isPerson());

        Facebook facebook = Facebook.getInstance();
        facebook.addPrivateKey(sk, identifier);
        facebook.addMeta(meta, identifier);

        User user = new User(identifier);
        user.dataSource = facebook;
        facebook.addUser(user);

        byte[] signature = user.sign(data);
        Assert.assertTrue(user.verify(data, signature));
        Log.info("signature OK!");

        byte[] ciphertext = user.encrypt(data);
        byte[] plaintext = user.decrypt(ciphertext);
        Assert.assertArrayEquals(data, plaintext);
        Log.info("decryption OK!");
    }

    @Test
    public void testEntity() {
        ID identifier = ID.getInstance("moky@4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
        Log.info("ID: " + identifier + ", detail: " + getIDInfo(identifier));

        Account account = new Account(identifier);
        Log.info("account: " + account);
        Assert.assertEquals(4049699527L, account.getNumber());

        User user = new User(identifier);
        Log.info("user: " + user);
        Assert.assertEquals(4049699527L, user.getNumber());

        if (account.equals(user)) {
            Log.info("same entity");
        }
    }
}

