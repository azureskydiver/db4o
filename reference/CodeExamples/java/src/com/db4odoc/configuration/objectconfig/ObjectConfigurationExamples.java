package com.db4odoc.configuration.objectconfig;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;


public class ObjectConfigurationExamples {
    private static final String DATABASE_FILE = "database.db4o";

    private static void setMinimalActivationDepth() {
        // #example: Set minimum activation depth
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().objectClass(Person.class).minimumActivationDepth(2);
        // #end example

        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE);
        container.close();
    }

    private static void callConstructor() {
        // #example: Call constructor
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().objectClass(Person.class).callConstructor(true);
        // #end example

        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE);
        container.close();
    }
}
