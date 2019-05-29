import chat.dim.crypto.PrivateKey;
import chat.dim.mkm.Profile;
import chat.dim.mkm.User;
import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.Meta;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class ImmortalsTest {

    private User loadBuiltInAccount(String filename) throws IOException, ClassNotFoundException {
        String jsonString = FileUtils.readTextFile(filename);
        Map<String, Object> dictionary = JsON.decode(jsonString);
        System.out.println(filename + ":" + dictionary);
        // ID
        ID identifier = ID.getInstance(dictionary.get("ID"));
        assert identifier != null;
        // meta
        Meta meta = Meta.getInstance(dictionary.get("meta"));
        assert meta != null && meta.matches(identifier);
        // profile
        Profile profile = Profile.getInstance(dictionary.get("profile"));
        assert profile != null;
        // private key
        PrivateKey privateKey = PrivateKey.getInstance(dictionary.get("privateKey"));
        if (meta.key.matches(privateKey)) {
            // store private key into keychain
        } else {
            throw new IllegalArgumentException("private key not match meta public key:" + privateKey);
        }
        // create user
        User user = new User(identifier);
        //user.dataSource = this;
        return user;
    }

    @Test
    public void testImmortals() throws IOException, ClassNotFoundException {
        // Immortal Hulk
        User hulk = loadBuiltInAccount("/mkm_hulk.js");
        Log.info(hulk.toString());
        // Monkey King
        User moki = loadBuiltInAccount("/mkm_moki.js");
        Log.info(moki.toString());
    }
}