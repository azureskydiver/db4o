/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.classmapping;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;


public class MappingExample {
	private static final String DB4O_FILE_NAME = "test.container";
	
	public static void main(String[] args) {
		storeObjects();
		retrieveObjects();
	}
	// end main

	private static void storeObjects(){
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Pilot pilot = new Pilot("Michael Schumacher", 100);
			container.store(pilot);
			pilot = new Pilot("Rubens Barichello", 99);
			container.store(pilot);
		} finally {
			container.close();
		}
	}
	// end storeObjects
	
	private static void retrieveObjects(){
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(Pilot.class).readAs(PilotReplacement.class);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Query query = container.query();
			query.constrain(PilotReplacement.class);
			ObjectSet result = query.execute();
			listResult(result);
		} finally {
			container.close();
		}
	}
	// end retrieveObjects
	
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
