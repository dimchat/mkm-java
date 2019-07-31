
import chat.dim.mkm.Account;
import chat.dim.mkm.User;
import chat.dim.mkm.entity.ID;
import org.junit.Test;

import java.io.IOException;

public class ImmortalsTest {

    private Facebook facebook = Facebook.getInstance();

    @Test
    public void testImmortals() throws IOException, ClassNotFoundException {
        // Immortal Hulk
        User hulk = Facebook.loadBuiltInAccount("/mkm_hulk.js");
        Log.info("hulk: " + hulk);

        Log.info("name: " + hulk.getName());
        Log.info("profile: " + facebook.getProfile(hulk.identifier));

        // Monkey King
        User moki = Facebook.loadBuiltInAccount("/mkm_moki.js");
        Log.info("moki: " + moki);

        Log.info("name: " + moki.getName());
        Log.info("profile: " + facebook.getProfile(moki.identifier));

        // Everyone
        Account anyone = new Account(ID.ANYONE);
        anyone.dataSource = facebook;
        Log.info("broadcast: " + anyone.identifier);
        Log.info("number: " + anyone.getNumber());
        Log.info("anyone: " + anyone);
        Log.info("is broadcast: " + anyone.identifier.isBroadcast());

        // Everyone
        Account everyone = new Account(ID.EVERYONE);
        everyone.dataSource = facebook;
        Log.info("broadcast: " + everyone.identifier);
        Log.info("number: " + everyone.getNumber());
        Log.info("everyone: " + everyone);
        Log.info("is broadcast: " + everyone.identifier.isBroadcast());
    }
}