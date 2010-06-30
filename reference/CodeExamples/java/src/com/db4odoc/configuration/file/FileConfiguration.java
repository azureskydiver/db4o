package com.db4odoc.configuration.file;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

import java.io.IOException;


public class FileConfiguration {

    public static void asynchronousSync(){
        // #example: Allow asynchronous synchronisation of the file-flushes
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.file().asynchronousSync(true);
        // #end example
        ObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");
        container.close();

    }

    public static void changeBlobPath(){
        // #example: Configure the blob-path
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        try {
            configuration.file().blobPath("myBlobDirectory");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // #end example
        ObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");
        container.close();

    }
    public static void reserveSpace(){
        // #example: Configure the growth size
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.file().databaseGrowthSize(4096);
        // #end example
        ObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");
        container.close();

    }
    public static void disableCommitRecovers(){
        // #example: Disable commit recovery
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.file().disableCommitRecovery();
        // #end example
        ObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");
        container.close();

    }
    public static void readOnlyMode(){
        // #example: Set read only mode
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.file().readOnly(true);
        // #end example
        ObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");
        container.close();

    }
    public static void recoveryMode(){
        // #example: Enable recovery mode to open a corrupted database
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.file().recoveryMode(true);
        // #end example
        ObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");
        container.close();
    }
    public static void reserveStorageSpace(){
        // #example: Reserve storage space
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.file().reserveStorageSpace(1024*1024);
        // #end example
        ObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");
        container.close();
    }
}
