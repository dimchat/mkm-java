package chat.dim.mkm;

import chat.dim.crypto.PublicKey;
import chat.dim.mkm.entity.Entity;
import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.Meta;

public class Account extends Entity {

    public Account(Account account) {
        super(account);
    }

    public Account(ID identifier) {
        super(identifier);
    }

    public PublicKey getPublicKey() {
        Meta meta = getMeta();
        if (meta == null) {
            return null;
        }
        return meta.key;
    }
}
