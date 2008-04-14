/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.aliases;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;

public class AliasExample {
	private static final String DB4O_FILE_NAME = "reference.db4o";
	private static TypeAlias tAlias;

	public static void main(String args[]) 
	{ 
		Configuration configuration = configureClassAlias();
		saveDrivers(configuration);
		removeClassAlias(configuration);
		getPilots(configuration);
//		savePilots(configuration);
//		configuration = configureAlias();
//		getObjectsWithAlias(configuration);
	}
	// end main
	
	private static Configuration configureClassAlias(){
		// create a new alias
		tAlias = new TypeAlias("com.db4odoc.aliases.Pilot","com.db4odoc.aliases.Driver");
		// add the alias to the db4o configuration
		Configuration configuration = Db4o.newConfiguration();
		configuration.addAlias(tAlias);
		// check how does the alias resolve
		System.out.println("Stored name for com.db4odoc.aliases.Driver: "+tAlias.resolveRuntimeName("com.db4odoc.aliases.Driver"));
		System.out.println("Runtime name for com.db4odoc.aliases.Pilot: "+tAlias.resolveStoredName("com.db4odoc.aliases.Pilot"));
		return configuration;
	}
	// end configureClassAlias
	
	
	private static void removeClassAlias(Configuration configuration){
		configuration.removeAlias(tAlias);
	}
	// end removeClassAlias
	
	private static void saveDrivers(Configuration configuration){
		new File(DB4O_FILE_NAME ).delete();
		ObjectServer server = Db4o.openServer(DB4O_FILE_NAME, 0xdb40);
		server.grantAccess("user", "password");
		ObjectContainer container = Db4o.openClient(configuration, "localhost", 0xdb40, "user", "password");
		try {
			Driver driver = new Driver("David Barrichello",99);
			container.store(driver);
			driver = new Driver("Kimi Raikkonen",100);
			container.store(driver);
		} finally {
			container.close();
			server.close();
		}
	}
	// end saveDrivers
	
	private static void savePilots(Configuration configuration){
		new File(DB4O_FILE_NAME ).delete();
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Pilot pilot = new Pilot("David Barrichello",99);
			container.store(pilot);
			pilot = new Pilot("Kimi Raikkonen",100);
			container.store(pilot);
		} finally {
			container.close();
		}
	}
	// end savePilots
	
	private static void getPilots(Configuration configuration){
//		ObjectServer server = Db4o.openServer(configuration, DB4O_FILE_NAME, 0);
//		ObjectContainer container = server.openClient();
		ObjectServer server = Db4o.openServer(DB4O_FILE_NAME, 0xdb40);
		server.grantAccess("user", "password");
		ObjectContainer container = Db4o.openClient(configuration, "localhost", 0xdb40, "user", "password");
		
		//ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			ObjectSet result = container.query(com.db4odoc.aliases.Pilot.class);
			listResult(result);
		} finally {
			container.close();
			server.close();
		}
	}
	// end getPilots
	
	private static void getObjectsWithAlias(Configuration configuration){
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			ObjectSet result = container.query(com.db4odoc.aliases.newalias.Pilot.class);
			listResult(result);
		} finally {
			container.close();
		}
	}
	// end getObjectsWithAlias

	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
	
	private static Configuration configureAlias() {
    	// com.db4odoc.aliases.* - package for the classes saved in the database
    	// com.db4odoc.aliases.newalias.* - runtime package
		WildcardAlias wAlias = new WildcardAlias("com.db4odoc.aliases.*","com.db4odoc.aliases.newalias.*");
		// add the alias to the configuration
		Configuration configuration = Db4o.newConfiguration();
		configuration.addAlias(wAlias);
		System.out.println("Stored name for com.db4odoc.aliases.newalias.Pilot: "+wAlias.resolveRuntimeName("com.db4odoc.aliases.newalias.Pilot"));
		System.out.println("Runtime name for com.db4odoc.aliases.Pilot: "+wAlias.resolveStoredName("com.db4odoc.aliases.Pilot"));
		return configuration;
    }
    // end configureAlias
}
