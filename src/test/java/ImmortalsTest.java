
import chat.dim.crypto.PrivateKey;
import chat.dim.crypto.impl.PrivateKeyImpl;
import chat.dim.format.JSON;
import chat.dim.mkm.User;
import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.Meta;
import chat.dim.mkm.entity.Profile;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ImmortalsTest {

    private Facebook facebook = Facebook.getInstance();

    @SuppressWarnings("unchecked")
    private User loadBuiltInAccount(String filename) throws IOException {
        String jsonString = Utils.readTextFile(filename);
        Map<String, Object> dictionary = JSON.decode(jsonString);

        // ID
        ID identifier = ID.getInstance(dictionary.get("ID"));
        assert identifier != null;
        // meta
        Meta meta = Meta.getInstance(dictionary.get("meta"));
        assert meta != null && meta.matches(identifier);
        facebook.addMeta(meta, identifier);
        // private key
        PrivateKey privateKey = PrivateKeyImpl.getInstance(dictionary.get("privateKey"));
        if (meta.key.matches(privateKey)) {
            // store private key into keychain
            facebook.addPrivateKey(privateKey, identifier);
        } else {
            throw new IllegalArgumentException("private key not match meta public key: " + privateKey);
        }
        // create user
        User user = new User(identifier);
        facebook.addUser(user);

        // profile
        Profile profile = Profile.getInstance(dictionary.get("profile"));
        List<String> names = (List<String>) profile.get("names");
        if (names != null) {
            profile.remove("names");
            if (names.size() > 0) {
                profile.setName(names.get(0));
            }
        }
        // sign profile
        profile.sign(privateKey);
        facebook.addProfile(profile);

        return user;
    }

    @Test
    public void testImmortals() throws IOException {
        // Immortal Hulk
        User hulk = loadBuiltInAccount("/mkm_hulk.js");
        Log.info("hulk: " + hulk);

        Log.info("name: " + hulk.getName());
        Log.info("profile: " + facebook.getProfile(hulk.identifier));

        // Monkey King
        User moki = loadBuiltInAccount("/mkm_moki.js");
        Log.info("moki: " + moki);

        Log.info("name: " + moki.getName());
        Log.info("profile: " + facebook.getProfile(moki.identifier));
    }
}