/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.replication;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.drs.*;


public class ReplicationExample {
	
	private final static String DTFILENAME="reference.db4o";
	private final static String HHFILENAME="handheld.db4o";
	
	public static void configureReplication(){
		Db4o.configure().generateUUIDs(ConfigScope.GLOBALLY); 
		Db4o.configure().generateVersionNumbers(ConfigScope.GLOBALLY);
	}
	// end configureReplication
	
	public static void configureReplicationPilot(){
		Db4o.configure().objectClass(Pilot.class).generateUUIDs(true); 
		Db4o.configure().objectClass(Pilot.class).generateVersionNumbers(true);
	}
	// end configureReplicationPilot
	
	public static void configureForExisting(){
		Db4o.configure().objectClass(Pilot.class).enableReplication(true); 
		try {
			com.db4o.defragment.Defragment.defrag("sample.db4o");
		} catch (IOException ex){
			System.out .println(ex.toString());
		}
	}
	// end configureForExisting
	
	public static void replicate(){
		ObjectContainer desktop=Db4o.openFile(DTFILENAME);
		ObjectContainer handheld=Db4o.openFile(HHFILENAME);
        //		 Setup a replication session
		ReplicationSession replication = Replication.begin(handheld, desktop);
		
		/*
		 * There is no need to replicate all the objects each time. 
		 * objectsChangedSinceLastReplication methods gives us 
		 * a list of modified objects
		 */
		ObjectSet changed =	replication.providerA().objectsChangedSinceLastReplication();
		
		while (changed.hasNext()) {
			replication.replicate(changed.next());
		}
		
		replication.commit();
	} 
	// end replicate
	
	public static void replicatePilots(){
		ObjectContainer desktop=Db4o.openFile(DTFILENAME);
		ObjectContainer handheld=Db4o.openFile(HHFILENAME);
		ReplicationSession replication = Replication.begin(handheld, desktop);
		ObjectSet changed =	replication.providerB().objectsChangedSinceLastReplication();
		
		/* Iterate through the changed objects,
		 * check if the name starts with "S" and replicate only those items
		 */
		while (changed.hasNext()) {
			if (changed instanceof Pilot) {
				if (((Pilot)changed).getName().startsWith("S")){
					replication.replicate(changed.next());
				}
			}
		}
		
		replication.commit();
	} 
	// end replicatePilots	
	
	public static void replicateBiDirectional(){
		ObjectContainer desktop=Db4o.openFile(DTFILENAME);
		ObjectContainer handheld=Db4o.openFile(HHFILENAME);
		ReplicationSession replication = Replication.begin(handheld, desktop);
		ObjectSet changed =	replication.providerA().objectsChangedSinceLastReplication();
		while (changed.hasNext()) {
					replication.replicate(changed.next());
		}
		// Add one more loop for bi-directional replication
		changed = replication.providerB().objectsChangedSinceLastReplication();
		while(changed.hasNext()) {    
			replication.replicate(changed.next());
		}
		
		replication.commit();
	} 
	// end replicateBiDirectional
}
