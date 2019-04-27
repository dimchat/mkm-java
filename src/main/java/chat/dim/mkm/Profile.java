package chat.dim.mkm;

import chat.dim.crypto.Dictionary;
import chat.dim.crypto.Utils;
import chat.dim.mkm.entity.ID;

import java.util.Map;

public class Profile extends Dictionary {

    public final ID identifier;

    public Profile(Map<String, Object> dictionary) {
        super(dictionary);
        this.identifier = ID.getInstance(dictionary.get("ID"));
    }

    public Profile(ID identifier) {
        super();
        this.identifier = identifier;
        dictionary.put("ID", identifier.toString());
    }

    @SuppressWarnings("unchecked")
    public static Profile getInstance(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Profile) {
            return (Profile) object;
        } else if (object instanceof Map) {
            return new Profile((Map<String, Object>) object);
        } else if (object instanceof String) {
            return new Profile(Utils.jsonDecode((String) object));
        } else {
            throw new IllegalArgumentException("unknown meta:" + object);
        }
    }

    public String getName() {
        return (String) dictionary.get("name");
    }

    public void setName(String name) {
        dictionary.put("name", name);
    }

    public String getAvatar() {
        return (String) dictionary.get("avatar");
    }

    public void setAvatar(String avatar) {
        dictionary.put("avatar", avatar);
    }
}
