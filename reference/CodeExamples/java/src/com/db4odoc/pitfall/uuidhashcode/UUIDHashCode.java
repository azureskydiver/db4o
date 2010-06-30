package com.db4odoc.pitfall.uuidhashcode;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

import java.io.File;
import java.util.UUID;


public class UUIDHashCode {
    private static final String DATABASE_FILE = "database.db4o";

    public static void main(String[] args) {
        new File(DATABASE_FILE).delete();
        storeObjectWithUUIDs(newObjectContainer(defaultConfiguration()));
        runTestWithConfiguration(constructorConfiguration());
        runTestWithConfiguration(defaultConfiguration());
    }

    private static EmbeddedConfiguration constructorConfiguration() {
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        // #example: call the constructor on UUIDs to fix its hashCode
        configuration.common().objectClass(UUID.class).callConstructor(true);
        // #end example
        return configuration;
    }

    private static EmbeddedConfiguration defaultConfiguration() {
        return Db4oEmbedded.newConfiguration();
    }

    private static void runTestWithConfiguration(EmbeddedConfiguration config) {
        timeContainsCheck(newObjectContainer(config));
    }

    private static void timeContainsCheck(ObjectContainer container) {
        UUIDContainer uuidContainer = container.query(UUIDContainer.class).get(0);
        long time = System.currentTimeMillis();
        assertContainsUUID(uuidContainer);
        System.out.println("Time used for adding= "+(System.currentTimeMillis()-time));


        container.close();
    }

    private static void assertContainsUUID(UUIDContainer uuidContainer) {
        for (UUID uuid : uuidContainer) {
            assertTrue(uuidContainer.contains(uuid)) ;   
        }
    }

    private static void assertTrue(boolean b) {
        if(!b){
            throw new AssertionError("Expected true");
        }
    }

    private static void storeObjectWithUUIDs(ObjectContainer container) {

        UUIDContainer uuidContainer = new UUIDContainer();
        for(int i=0;i<10000;i++){
            uuidContainer.add(UUID.randomUUID());
        }
        container.store(uuidContainer);

        container.close();
    }

    private static ObjectContainer newObjectContainer(EmbeddedConfiguration embeddedConfiguration) {
        return Db4oEmbedded.openFile(embeddedConfiguration, DATABASE_FILE);
    }
}
