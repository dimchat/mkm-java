package chat.dim.mkm.entity;

public interface MetaDataSource {

    /**
     *  Get meta for entity ID
     *
     * @param identifier - entity ID
     * @return meta object
     */
    public Meta getMeta(ID identifier);
}
