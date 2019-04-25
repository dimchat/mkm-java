package chat.dim.mkm;

import chat.dim.crypto.Utils;
import chat.dim.mkm.entity.ID;

import java.util.HashMap;
import java.util.Map;

public class Profile {

    private final Map<String, Object> dictionary;

    public final ID identifier;

    public Profile(Profile profile) {
        super();
        this.dictionary = profile.dictionary;
        this.identifier = profile.identifier;
    }

    public Profile(Map<String, Object> dictionary) {
        super();
        this.dictionary = dictionary;
        this.identifier = ID.getInstance(dictionary.get("ID"));
    }

    public Profile(ID identifier) {
        super();
        Map<String, Object> map = new HashMap<>();
        map.put("ID", identifier.toString());
        this.dictionary = map;
        this.identifier = identifier;
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

    public Map<String, Object> toDictionary() {
        return dictionary;
    }

    public String toString() {
        return dictionary.toString();
    }

    public String toJSONString() {
        return Utils.jsonEncode(dictionary);
    }

    public boolean equals(Profile profile) {
        return dictionary.equals(profile.dictionary);
    }

    public boolean equals(Map map) {
        return dictionary.equals(map);
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
