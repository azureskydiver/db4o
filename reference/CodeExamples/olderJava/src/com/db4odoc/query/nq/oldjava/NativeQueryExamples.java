package com.db4odoc.query.nq.oldjava;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;

import java.io.File;
import java.util.Iterator;


public class NativeQueryExamples {
    private static final String DATABASE_FILE = "database.db4o";

    public static void main(String[] args) {
        cleanUp();
        final EmbeddedConfiguration cfg = Db4oEmbedded.newConfiguration();
        ObjectContainer container = Db4oEmbedded.openFile(cfg,DATABASE_FILE);
        try {
            storeData(container);

            java14(container);
            java11(container);
        } finally {
            container.close();
        }
    }

    private static void java14(ObjectContainer container) {
        // #example: Query on Java 1.4
        final ObjectSet result = container.query(new Predicate() {
            public boolean match(Pilot pilot) {
                return pilot.getName().equals("John");
            }
        });
        // #end example

        listResult(result);
    }
    private static void java11(ObjectContainer container) {
        // #example: Queries on Java 1.1
        final ObjectSet result = container.query(new AllJohns());
        // #end example

        listResult(result);
    }


    private static void listResult(ObjectSet result) {
        for (Iterator it = result.iterator();it.hasNext();) {
            Pilot pilot = (Pilot) it.next();
            System.out.println(pilot);

        }
    }

    private static void cleanUp() {
        new File(DATABASE_FILE).delete();
    }

    private static void storeData(ObjectContainer container) {
        Pilot john = new Pilot("John",42);
        Pilot joanna = new Pilot("Joanna",45);
        Pilot jenny = new Pilot("Jenny",21);
        Pilot rick = new Pilot("Rick",33);
        Pilot juliette = new Pilot("Juliette",33);

        container.store(new Car(john,"Ferrari"));
        container.store(new Car(joanna,"Mercedes"));
        container.store(new Car(jenny,"Volvo"));
        container.store(new Car(rick,"Fiat"));
        container.store(new Car(juliette,"Suzuki"));

    }
}
