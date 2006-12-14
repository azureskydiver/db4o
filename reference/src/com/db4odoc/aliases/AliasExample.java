/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.aliases;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.TypeAlias;
import com.db4o.config.WildcardAlias;

public class AliasExample {
	private static final String YAPFILENAME = "formula1.yap";
	private static TypeAlias tAlias;

	public static void main(String args[]) 
	{ 
		configureClassAlias();
		saveDrivers();
		removeClassAlias();
		getPilots();
		savePilots();
		configureAlias();
		getObjectsWithAlias();
	}
	// end main
	
	public static void configureClassAlias(){
		// create a new alias
		tAlias = new TypeAlias("com.db4odoc.aliases.Pilot","com.db4odoc.aliases.Driver");
		// add the alias to the db4o configuration 
		Db4o.configure().addAlias(tAlias);
		// check how does the alias resolve
		System.out.println("Stored name for com.db4odoc.aliases.Driver: "+tAlias.resolveRuntimeName("com.db4odoc.aliases.Driver"));
		System.out.println("Runtime name for com.db4odoc.aliases.Pilot: "+tAlias.resolveStoredName("com.db4odoc.aliases.Pilot"));
	}
	// end configureClassAlias
	
	
	public static void removeClassAlias(){
		Db4o.configure().removeAlias(tAlias);
	}
	// end removeClassAlias
	
	public static void saveDrivers(){
		new File(YAPFILENAME ).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Driver driver = new Driver("David Barrichello",99);
			db.set(driver);
			driver = new Driver("Finn Kimi Raikkonen",100);
			db.set(driver);
		} finally {
			db.close();
		}
	}
	// end saveDrivers
	
	public static void savePilots(){
		new File(YAPFILENAME ).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Pilot pilot = new Pilot("David Barrichello",99);
			db.set(pilot);
			pilot = new Pilot("Finn Kimi Raikkonen",100);
			db.set(pilot);
		} finally {
			db.close();
		}
	}
	// end savePilots
	
	public static void getPilots(){
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet result = db.query(com.db4odoc.aliases.Pilot.class);
			listResult(result);
		} finally {
			db.close();
		}
	}
	// end getPilots
	
	public static void getObjectsWithAlias(){
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet result = db.query(com.db4odoc.aliases.newalias.Pilot.class);
			listResult(result);
		} finally {
			db.close();
		}
	}
	// end getObjectsWithAlias

    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
	
    public static void configureAlias() {
    	// com.db4odoc.aliases.* - package for the classes saved in the database
    	// com.db4odoc.aliases.newalias.* - runtime package
		WildcardAlias wAlias = new WildcardAlias("com.db4odoc.aliases.*","com.db4odoc.aliases.newalias.*");
		Db4o.configure().addAlias(wAlias);
		System.out.println("Stored name for com.db4odoc.aliases.newalias.Pilot: "+wAlias.resolveRuntimeName("com.db4odoc.aliases.newalias.Pilot"));
		System.out.println("Runtime name for com.db4odoc.aliases.Pilot: "+wAlias.resolveStoredName("com.db4odoc.aliases.Pilot"));
    }
    // end configureAlias
}
