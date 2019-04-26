package chat.dim.mkm;

import chat.dim.crypto.PublicKey;
import chat.dim.mkm.entity.Entity;
import chat.dim.mkm.entity.ID;
import chat.dim.mkm.entity.Meta;

public class Account extends Entity {

    public final PublicKey publicKey;

    public Account(Account account) {
        super(account);
        this.publicKey = account.publicKey;
    }

    public Account(ID identifier, PublicKey publicKey) {
        super(identifier);
        this.publicKey = publicKey;
    }

    public Account(ID identifier) {
        this(identifier, null);
    }

    public PublicKey getPublicKey() {
        if (this.publicKey != null) {
            return this.publicKey;
        }
        // get from meta
        Meta meta = getMeta();
        return meta == null ? null : meta.key;
    }
}
