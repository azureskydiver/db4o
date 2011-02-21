package com.db4odoc.tp.rollback;


import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.ta.DeactivatingRollbackStrategy;
import com.db4o.ta.TransparentPersistenceSupport;

public class RollbackExample {
    public static void main(String[] args) {
        // #example: Configure rollback strategy
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common()
                .add(new TransparentPersistenceSupport(new DeactivatingRollbackStrategy()));
        // #end example
        ObjectContainer container = Db4oEmbedded.openFile(configuration,"database.db4o");
        try {
            storePilot(container);

            // #example: Rollback with rollback strategy
            Pilot pilot = container.query(Pilot.class).get(0);
            pilot.setName("NewName");
            // Rollback
            container.rollback();
            // Now the pilot has the old name again
            System.out.println(pilot.getName());
            // #end example
        } finally {
            container.close();
        }
    }

    private static void storePilot(ObjectContainer container) {
        container.store(new Pilot("John"));
        container.commit();
    }

}
