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

import java.util.ArrayList;
import java.util.List;

public class EntityTest {

    private void log(String msg) {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String method = traces[2].getMethodName();
        int line = traces[2].getLineNumber();
        System.out.println("[" + method + ":" + line + "] " + msg);
    }

    @Test
    public void testAddress() {
        Address address;

        address = Address.getInstance("4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk");
        log("address: " + address);
        log("number: " + address.code);
        Assert.assertEquals(1840839527L, address.code);

        address = Address.getInstance("4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
        log("address: " + address);
        log("number: " + address.code);
        Assert.assertEquals(4049699527L, address.code);
    }

    @Test
    public void testID() {
        ID identifier;

        identifier = ID.getInstance("moki@4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk");
        log("ID: " + identifier);
        log("number: " + identifier.getNumber());
        Assert.assertEquals(1840839527L, identifier.getNumber());

        identifier = ID.getInstance("moky@4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
        log("ID: " + identifier);
        log("number: " + identifier.getNumber());
        Assert.assertEquals(4049699527L, identifier.getNumber());

        List<ID> array = new ArrayList<>();
        array.add(identifier);
        array.add(identifier);
        log("list<ID>:" + array);

        Assert.assertTrue(identifier.isValid());
    }

    @Test
    public void testMeta() throws ClassNotFoundException {
        PrivateKey sk = PrivateKey.create(PrivateKey.RSA);
        PublicKey pk = sk.getPublicKey();
        String seed = "moky";

        Meta meta = new Meta(sk, seed);
        log("meta:" +meta);
        Assert.assertTrue(meta.matches(pk));

        ID identifier = meta.buildID(NetworkType.Main);
        log("ID:" + identifier);
        Assert.assertTrue(meta.matches(identifier));
        Assert.assertTrue(meta.matches(identifier.address));

        Assert.assertTrue(identifier.getType().isCommunicator());
        Assert.assertTrue(identifier.getType().isPerson());

        User user = new User(identifier, sk);
        PublicKey pk2 = user.getPublicKey();
        PrivateKey sk2 = user.getPrivateKey();
        Assert.assertTrue(pk2.matches(sk));
        Assert.assertTrue(sk2.equals(sk));
    }

    @Test
    public void testEntity() {
        ID identifier = ID.getInstance("moky@4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");

        Account account = new Account(identifier);
        log("account:" + account);
        Assert.assertEquals(4049699527L, account.getNumber());

        User user = new User(identifier);
        log("user:" + user);
        Assert.assertEquals(4049699527L, user.getNumber());

        if (account.equals(user)) {
            log("same entity");
        }
    }
}