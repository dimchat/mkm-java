
import org.junit.Test;

import java.io.IOException;

import chat.dim.mkm.User;
import chat.dim.mkm.LocalUser;
import chat.dim.mkm.ID;

public class ImmortalsTest {

    private Facebook facebook = Facebook.getInstance();

    @Test
    public void testImmortals() throws IOException, ClassNotFoundException {
        // Immortal Hulk
        LocalUser hulk = Facebook.loadBuiltInAccount("/mkm_hulk.js");
        Log.info("hulk: " + hulk);

        Log.info("name: " + hulk.getName());
        Log.info("profile: " + facebook.getProfile(hulk.identifier));

        // Monkey King
        LocalUser moki = Facebook.loadBuiltInAccount("/mkm_moki.js");
        Log.info("moki: " + moki);

        Log.info("name: " + moki.getName());
        Log.info("profile: " + facebook.getProfile(moki.identifier));

        // Everyone
        User anyone = new User(ID.ANYONE);
        anyone.dataSource = facebook;
        Log.info("broadcast: " + anyone.identifier);
        Log.info("number: " + anyone.getNumber());
        Log.info("anyone: " + anyone);
        Log.info("is broadcast: " + anyone.identifier.isBroadcast());

        // Everyone
        User everyone = new User(ID.EVERYONE);
        everyone.dataSource = facebook;
        Log.info("broadcast: " + everyone.identifier);
        Log.info("number: " + everyone.getNumber());
        Log.info("everyone: " + everyone);
        Log.info("is broadcast: " + everyone.identifier.isBroadcast());
    }
}