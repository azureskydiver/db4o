/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.quickstart.simple;


import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationSession;


public class Simple {

	public static void main(String[] args) {
		DoOneWayReplcation();
		DoBiDirectionalReplication();
		DoSelectiveReplication();
	}

	private static void DoSelectiveReplication() {
		
		configureDb4oForReplication();

		// Open the source db4o database
		ObjectContainer handheld = openDb("handheld.yap");
		
		// Store a few objects
		storeSomePilots(handheld);
		
		// Open the destination db4o database
		ObjectContainer desktop = openDb("desktop.yap");

		// Display contents of both databases before replication
		displayContents("Selective Replication", "Before", handheld, desktop);
		
		// Setup a replication session
		ReplicationSession replication = Replication.begin(handheld, desktop);
		
		// Query for objects changed from db4o
		ObjectSet changed =	replication.providerA().objectsChangedSinceLastReplication();
		
		// Iterate changed objects, check if the name starts with "S" and replicate only those items
		while (changed.hasNext()) {
			Pilot p = (Pilot)changed.next();
			if(p._name.startsWith("S"))
				replication.replicate(p);
		}
		
		// commit all of the changes to both databases
		replication.commit();

		// Display contents of both databases before replication
		displayContents("", "After", handheld, desktop);

		// Close the databases
		closeDb(handheld);
		closeDb(desktop);
	}

	private static void DoBiDirectionalReplication() {

		configureDb4oForReplication();

		// Open the source db4o database
		ObjectContainer handheld = openDb("handheld.yap");

		// Store a few objects		
		storeSomePilots(handheld);
		
		// Open the destination db4o database
		ObjectContainer desktop = openDb("desktop.yap");

		// Store a few objects		
		storeSomeMorePilots(desktop);

		// Display contents of both databases before replication
		displayContents("Bi-Directional", "Before", handheld, desktop);
		
		// Setup a replication session
		ReplicationSession replication = Replication.begin(handheld, desktop);
		
		// Query for objects changed from db4o (ProviderA == handheld)
		ObjectSet changed =	replication.providerA().objectsChangedSinceLastReplication();
		
		// Iterate changed objects, replicate them
		while (changed.hasNext())
			replication.replicate(changed.next());

		// For Bi-Directional Replication we just need to add this extra loop based on 
		// the destination database's changed objects.

		// Query for objects changed on the desktop (ProviderB == desktop)
		changed = replication.providerB().objectsChangedSinceLastReplication();
		
		// Iterate changed objects, replicate them
		while(changed.hasNext())		
			replication.replicate(changed.next());
		
		// commit all of the changes to both databases
		replication.commit();
		
		// Display the contents
		displayContents("", "After", handheld, desktop);
		
		// Close the databases
		closeDb(handheld);
		closeDb(desktop);
	}

	private static void displayContents(String methodname, String pointintime, ObjectContainer handheld, ObjectContainer desktop) {
		if (methodname != "") {
			System.out.println(methodname + " Replication");
			System.out.println();
		}
		System.out.println(pointintime + " Replication");
		System.out.println();
		displayContentsOf("Contents of Handheld", handheld);
		displayContentsOf("Contents of Desktop", desktop);
	}

	private static void storeSomeMorePilots(ObjectContainer db) {
		db.set(new Pilot("Peter van der Merwe", 37));
		db.set(new Pilot("Albert Kwan", 30));
	}

	private static void displayContentsOf(String heading, ObjectContainer db) {
		System.out.println(heading);
		System.out.println();
		ObjectSet result = db.get(new Pilot());
		listResult(result);
	}

	private static void closeDb(ObjectContainer db) {
		db.close();
	}

	private static ObjectContainer openDb(String dbname) {
		new File(dbname).delete();
		ObjectContainer db = Db4o.openFile(dbname);
		return db;
	}

	private static void configureDb4oForReplication() {
		// Replication requires UUIDs and VersionNumbers
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
	}

	private static void DoOneWayReplcation() {

		configureDb4oForReplication();

		// Open the source db4o database
		ObjectContainer handheld = openDb("handheld.yap");
		
		// Store a few objects
		storeSomePilots(handheld);
		
		// Open the destination db4o database
		ObjectContainer desktop = openDb("desktop.yap");

		// Display contents of both databases before replication
		displayContents("One-way Replication", "Before", handheld, desktop);
		
		// Setup a replication session
		ReplicationSession replication = Replication.begin(handheld, desktop);
		
		// Query for objects changed from db4o
		ObjectSet changed =	replication.providerA().objectsChangedSinceLastReplication();
		
		// Iterate changed objects, replicate them
		while (changed.hasNext())
			replication.replicate(changed.next());
		
		// commit all of the changes to both databases
		replication.commit();

		// Display contents of both databases before replication
		displayContents("", "After", handheld, desktop);

		// Close the databases
		closeDb(handheld);
		closeDb(desktop);
	}

	private static void storeSomePilots(ObjectContainer db) {
		db.set(new Pilot("Scott Felton", 52));
		db.set(new Pilot("Frank Green", 45));
	}
	
    public static void listResult(ObjectSet result) {
        while(result.hasNext()) {
            System.out.println(result.next());
        }
        System.out.println();
    }
}
