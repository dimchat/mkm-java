package chat.dim.mkm.entity;

import chat.dim.mkm.Profile;

public interface IEntityDataSource {

    public Meta getMeta(Entity entity);

    public Profile getProfile(Entity entity);

    public String getName(Entity entity);
}
