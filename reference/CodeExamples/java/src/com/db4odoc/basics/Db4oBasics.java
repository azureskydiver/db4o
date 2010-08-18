package com.db4odoc.basics;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;


public class Db4oBasics {
    public static void main(String[] args) {
        openAndCloseTheContainer();

        ObjectContainer container = Db4oEmbedded.openFile("databaseFile.db4o");
        try {
            storeObject(container);
            deleteObject(container);
        } finally {
            container.close();
        }
    }

    private static void storeObject(ObjectContainer container) {
        // #example: Store a object
        Pilot pilot = new Pilot("Joe");
        container.store(pilot);
        // #end example
    }

    private static void deleteObject(ObjectContainer container) {
        final Pilot pilot = container.query(Pilot.class).get(0);
        // #example: Delete a object
        container.delete(pilot);
        // #end example
    }

    private static void openAndCloseTheContainer() {
        // #example: Open the object container to use the database
        ObjectContainer container = Db4oEmbedded.openFile("databaseFile.db4o");
        try {
            // use the object container
        } finally {
            container.close();
        }
        // #end example
    }
}
