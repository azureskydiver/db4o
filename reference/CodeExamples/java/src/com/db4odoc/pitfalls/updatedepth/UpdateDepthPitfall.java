package com.db4odoc.pitfalls.updatedepth;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;

import java.io.File;

public class UpdateDepthPitfall {
    public static final String DATABASE_FILE = "database.db4o";

    public static void main(String[] args) {
        cleanUp();
        prepareDeepObjGraph();


        toLowUpdateDeph();
        updateDepth();
    }

    private static void toLowUpdateDeph() {
        // #example: Update doesn't work
        ObjectContainer container = Db4oEmbedded.openFile(DATABASE_FILE);
        try {
            Person jodie = queryForJodie(container);
            jodie.add(new Person("Jamie"));
            // Remember that a collection is also a regular object
            // so with the default-update depth of one, only the changes
            // on the person-object are stored, but not the changes on
            // the friend-list.
            container.store(jodie);
        } finally {
            container.close();
        }
        container = Db4oEmbedded.openFile(DATABASE_FILE);
        try {
            Person jodie = queryForJodie(container);
            for (Person person : jodie.getFriends()) {
                // the added friend is gone, because the update-depth is to low
                System.out.println("Friend="+person.getName());
            }
        } finally {
            container.close();
        }
        // #end example
    }
    private static void updateDepth() {
        // #example: A higher update depth fixes the issue
        EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
        config.common().updateDepth(2);
        ObjectContainer container = Db4oEmbedded.openFile(config,DATABASE_FILE);
        // #end example
        try {
            Person jodie = queryForJodie(container);
            jodie.add(new Person("Jamie"));
            container.store(jodie);
        } finally {
            container.close();
        }
        config = Db4oEmbedded.newConfiguration();
        config.common().updateDepth(2);
        container = Db4oEmbedded.openFile(DATABASE_FILE);
        try {
            Person jodie = queryForJodie(container);
            for (Person person : jodie.getFriends()) {
                // the added friend is gone, because the update-depth is to low
                System.out.println("Friend="+person.getName());
            }
        } finally {
            container.close();
        }
    }

    private static void cleanUp() {
        new File(DATABASE_FILE).delete();
    }

    private static Person queryForJodie(ObjectContainer container) {
        return container.query(new Predicate<Person>() {
            @Override
            public boolean match(Person o) {
                return o.getName().equals("Jodie");
            }
        }).get(0);
    }

    private static void prepareDeepObjGraph() {
        ObjectContainer container = Db4oEmbedded.openFile(DATABASE_FILE);
        try {
            Person jodie = new Person("Jodie");

            jodie.add(new Person("Joanna"));
            jodie.add(new Person("Julia"));
            container.store(jodie);
        } finally {
            container.close();
        }
    }

}
