package chat.dim.mkm.entity;

import chat.dim.mkm.Profile;

public interface EntityDataSource {

    /**
     *  Get meta for entity (call 'metaForID:' of MetaDataSource instead)
     *
     * @param entity - entity object
     * @return meta object
     */
    Meta getMeta(Entity entity);

    /**
     *  Get profile for entity
     *
     * @param entity - entity object
     * @return profile object
     */
    Profile getProfile(Entity entity);

    /**
     *  Get entity name
     *
     * @param entity - entity object
     * @return entity name
     */
    String getName(Entity entity);
}
