/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.replication;

import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationSession;


public class ReplicationExample {
	public final static String DTFILENAME="formula1.yap";
	public final static String HHFILENAME="handheld.yap";
	
	public static void configureReplication(){
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE); 
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
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
			com.db4o.tools.defragment.Defragment.defrag("sample.yap");
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
