import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.PublicKey;
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
import java.util.List;

public class EntityTest {

    @Test
    public void testAddress() {
        Address address;

        address = Address.getInstance("4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk");
        Log.info("address: " + address);
        Log.info("number: " + address.code);
        Assert.assertEquals(1840839527L, address.code);

        address = Address.getInstance("4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
        Log.info("address: " + address);
        Log.info("number: " + address.code);
        Assert.assertEquals(4049699527L, address.code);

        NetworkType robot = NetworkType.Robot;
        Log.info("robot type:" + robot.toByte());
        Assert.assertTrue((byte)0xC8 == robot.toByte());
    }

    @Test
    public void testID() {
        ID identifier;

        identifier = ID.getInstance("moki@4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk");
        Log.info("ID: " + identifier);
        Log.info("number: " + identifier.getNumber());
        Assert.assertEquals(1840839527L, identifier.getNumber());

        identifier = ID.getInstance("moky@4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
        Log.info("ID: " + identifier);
        Log.info("number: " + identifier.getNumber());
        Assert.assertEquals(4049699527L, identifier.getNumber());

        Assert.assertEquals(identifier, new ID("moky@4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ/home"));

        List<ID> array = new ArrayList<>();
        array.add(identifier);
        array.add(identifier);
        Log.info("list<ID>:" + array);

        Assert.assertTrue(identifier.isValid());
    }

    @Test
    public void testMeta() throws ClassNotFoundException {
        PrivateKey sk = PrivateKey.create(PrivateKey.RSA);
        PublicKey pk = sk.getPublicKey();
        String seed = "moky";
        byte[] data = seed.getBytes(Charset.forName("UTF-8"));

        byte[] fingerprint = sk.sign(data);
        Meta meta = new Meta(Meta.VersionDefault, pk, seed, fingerprint);
        Log.info("meta:" +meta);
        Assert.assertTrue(meta.matches(pk));

        ID identifier = meta.generateID(NetworkType.Main);
        Log.info("ID:" + identifier);
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

        Account account = new Account(identifier);
        Log.info("account:" + account);
        Assert.assertEquals(4049699527L, account.getNumber());

        User user = new User(identifier);
        Log.info("user:" + user);
        Assert.assertEquals(4049699527L, user.getNumber());

        if (account.equals(user)) {
            Log.info("same entity");
        }
    }
}