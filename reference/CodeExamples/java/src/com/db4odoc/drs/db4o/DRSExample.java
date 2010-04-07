package com.db4odoc.drs.db4o;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.ConfigScope;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationSession;

import java.io.File;

public class DRSExample {
    public static final String DESKTOP_DATABASE = "desktopDatabase.db4o";
    public static final String MOBILE_DATABASE = "mobileDatabase.db4o";

	public static void main(String[] args){
		oneWayReplicationExample();
	           	
	}

    private static void oneWayReplicationExample() {
        deleteDatabases();
        ObjectContainer desktopDatabase = openDatabase(DESKTOP_DATABASE);
        storeObjectsIn(desktopDatabase);

        ObjectContainer mobileDatabase = openDatabase(MOBILE_DATABASE);

        //#snippet: One direction replication
        ReplicationSession replication = Replication.begin(desktopDatabase, mobileDatabase);
        ObjectSet changes = replication.providerA().objectsChangedSinceLastReplication();
        while(changes.hasNext()){
            Object changedObject = changes.next();
            replication.replicate(changedObject);
        }
        replication.commit();     
        //#endsnippet


        printPilots(mobileDatabase);

        desktopDatabase.close();
        mobileDatabase.close();
    }

    private static void printPilots(ObjectContainer mobileDatabase) {
        ObjectSet<Pilot> pilotsOnMobileDevice = mobileDatabase.query(Pilot.class);
        for (Pilot pilot : pilotsOnMobileDevice) {
            System.out.println(pilot);
        }
    }

    private static void storeObjectsIn(ObjectContainer db) {
        db.store(new Pilot("John",100));
        db.store(new Pilot("Max",200));
        db.store(new Pilot("Joe",100));
        db.commit();
    }

    private static ObjectContainer openDatabase(String fileName) {
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.file().generateUUIDs(ConfigScope.GLOBALLY);
        configuration.file().generateVersionNumbers(ConfigScope.GLOBALLY);
        return Db4oEmbedded.openFile(configuration, fileName);
    }

    private static void deleteDatabases() {
        new File(DESKTOP_DATABASE).delete();
        new File(MOBILE_DATABASE).delete();
    }

}
