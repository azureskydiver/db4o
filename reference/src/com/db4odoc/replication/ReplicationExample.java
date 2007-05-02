/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.replication;

import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.ConfigScope;
import com.db4o.config.Configuration;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationSession;


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
		
		// Iterate changed objects, check if the name starts with "S" and replicate only those items
		while (changed.hasNext()) {
			replication.replicate(changed.next());
		}
		
		replication.commit();
	} 
	// end replicate	
}
