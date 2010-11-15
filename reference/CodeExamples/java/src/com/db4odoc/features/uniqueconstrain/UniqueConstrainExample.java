package com.db4odoc.features.uniqueconstrain;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.constraints.UniqueFieldValueConstraintViolationException;


public class UniqueConstrainExample {

    public static void main(String[] args) {

        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        // #example: Add the index the field and then the unique constrain
        configuration.common().objectClass(UniqueId.class).objectField("id").indexed(true);
        configuration.common().add(new UniqueFieldValueConstraint(UniqueId.class, "id"));
        // #end example
        ObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");
        try {
            container.store(new UniqueId(44));
            // #example: Violating the constrain throws an exception
            container.store(new UniqueId(42));
            container.store(new UniqueId(42));
            try {
                container.commit();
            } catch (UniqueFieldValueConstraintViolationException e) {
                e.printStackTrace();
            }
            // #end example
        } finally {
            container.close();
        }
    }

    private static class UniqueId {
        private final int id;

        private UniqueId(int id) {
            this.id = id;
        }


    }
}
