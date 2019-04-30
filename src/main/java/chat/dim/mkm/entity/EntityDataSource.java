package chat.dim.mkm.entity;

import chat.dim.mkm.Profile;

public interface EntityDataSource {

    /**
     *  Get meta for entity (call 'metaForID:' of MetaDataSource instead)
     *
     * @param entity - entity object
     * @return meta object
     */
    public Meta getMeta(Entity entity);

    /**
     *  Get profile for entity
     *
     * @param entity - entity object
     * @return profile object
     */
    public Profile getProfile(Entity entity);

    /**
     *  Get entity name
     *
     * @param entity - entity object
     * @return entity name
     */
    public String getName(Entity entity);
}
