package com.db4odoc.configuration.io;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.io.*;


public class IOConfigurationExamples {

    public static void specifyGrowStrategyForMemoryStorage(){
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        // #example: Using memory-storage with constant grow strategy
        GrowthStrategy growStrategy = new ConstantGrowthStrategy(100);
        MemoryStorage memory = new MemoryStorage(growStrategy);
        configuration.file().storage(memory);
        // #end example
        EmbeddedObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");

    }
    public static void usingMemoryStorage(){
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        // #example: Using memory-storage
        MemoryStorage memory = new MemoryStorage();
        configuration.file().storage(memory);
        // #end example
        EmbeddedObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");

    }

    public static void storageStack(){
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        // #example: You stack up different storage-decorator to add functionality
        // the basic file storage
        Storage fileStorage = new FileStorage();
        // add your own decorator
        Storage myStorageDecorator = new MyStorageDecorator(fileStorage);
        // add caching to the storage
        Storage storageWithCaching = new CachingStorage(myStorageDecorator);
        // finally configure db4o with our storage-stack
        configuration.file().storage(storageWithCaching);
        // #end example
        EmbeddedObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");
        
    }


    /**
     * This decorator does nothing. It's just used as an example
     */
    private static class MyStorageDecorator extends StorageDecorator{

        public MyStorageDecorator(Storage storage) {
            super(storage);
        }
    }
}
