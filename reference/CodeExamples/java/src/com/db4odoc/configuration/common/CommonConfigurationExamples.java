package com.db4odoc.configuration.common;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.SimpleNameProvider;
import com.db4o.ta.TransparentPersistenceSupport;

import java.io.File;


public class CommonConfigurationExamples {
    private static final String DATABASE_FILE = "database.db4o";

   

    private static void exampleForCommonConfig() {
        // #example: change activation depth
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().activationDepth(2);
        // other configurations...

        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE);
        // #end example
        container.close();
    }
    private static void addTransparentPersistence() {
        // #example: add an additional configuration item
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().add(new TransparentPersistenceSupport());
        // other configurations...

        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE);
        // #end example
        container.close();
    }

    private static void internStrings() {
        // #example: intern strings
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().internStrings(true);
        // #end example

        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE);

        container.close();
    }


    private static void nameProvider() {
        // #example: set a name-provider
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().nameProvider(new SimpleNameProvider("Database"));
        // #end example

        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE);
        
        container.close();
    }


    private static void changeWeakReferenceCollectionIntervall() {
        // #example: change weak reference collection interval
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().weakReferenceCollectionInterval(10*1000);
        // #end example

        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE);

        container.close();
    }

    private static void markTransient() {
        cleanUp();

        // #example: add an transient marker annotatin
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().markTransient(TransientMarker.class.getName());
        // #end example

        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE);
        container.store(new WithTransient());
        container.close();

        readWithTransientMarker();

        cleanUp();
    }

    private static void cleanUp() {
        new File(DATABASE_FILE).delete();
    }

    private static void readWithTransientMarker() {
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().markTransient(TransientMarker.class.getName());
        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE);
        WithTransient instance = container.query(WithTransient.class).get(0);

        assertTransientNotStored(instance);

        container.close();
    }

    private static void assertTransientNotStored(WithTransient instance) {
        if(null!=instance.getTransientString()){
            throw new RuntimeException("Transient was stored!");
        }
    }


    private static class WithTransient{
        @TransientMarker
        private String transientString = "New";

        public String getTransientString() {
            return transientString;
        }

        public void setTransientString(String transientString) {
            this.transientString = transientString;
        }
    }

}
