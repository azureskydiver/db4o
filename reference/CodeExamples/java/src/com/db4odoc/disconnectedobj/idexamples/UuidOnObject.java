package com.db4odoc.disconnectedobj.idexamples;

import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;

import java.util.UUID;


public class UuidOnObject implements IdExample<String> {

    public static IdExample<String> create() {
        return new UuidOnObject();
    }

    public String idForObject(Object obj, ObjectContainer container) {
        // #example: get the uuid
        IDHolder uuidHolder = (IDHolder)obj;
        String uuid = uuidHolder.getObjectId();
        // #end example
        return uuid;
    }

    public IDHolder objectForID(final String idForObject, ObjectContainer container) {
        // #example: get an object its UUID
        IDHolder object= container.query(new Predicate<IDHolder>() {
            @Override
            public boolean match(IDHolder o) {
                return o.getObjectId().equals(idForObject);
            }
        }).get(0);
        // #end example
        return object;
    }

    public void configure(EmbeddedConfiguration configuration) {
        // #example: index the uuid-field
        configuration.common().objectClass(IDHolder.class).objectField("uuid").indexed(true);
        // #end example
    }

    public void registerEventOnContainer(ObjectContainer container) {
        // no events required for internal ids
    }
}
