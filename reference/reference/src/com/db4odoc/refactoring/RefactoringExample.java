/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.refactoring;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;

public class RefactoringExample {
	
	private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void main(String[] args) {
		System.out.println("Correct sequence of actions: ");
		setObjects();
		checkDB();
		changeClass();
		setNewObjects();
		retrievePilotNew();
		
	/*	System.out.println("Incorrect sequence of actions: ");
		setObjects();
		checkDB();
		setNewObjects();
		changeClass();
		retrievePilotNew();*/
		
	}
	// end main

	private static void setObjects(){
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Pilot pilot = new Pilot("Rubens Barrichello");
			container.store(pilot);
			pilot = new Pilot("Michael Schumacher");
			container.store(pilot);
		} finally {
			container.close();
		}
	}
	// end setObjects
	
	private static void setNewObjects(){
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			PilotNew pilot = new PilotNew("Rubens Barrichello",99);
			container.store(pilot);
			pilot = new PilotNew("Michael Schumacher",100);
			container.store(pilot);
		} finally {
			container.close();
		}
	}
	// end setNewObjects
	
	private static void checkDB(){
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result=container.queryByExample(new Object());
	        listResult(result);
		} finally {
			container.close();
		}
	}
	// end checkDB
	
	private static void changeClass(){
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(Pilot.class).rename("com.db4odoc.f1.refactoring.PilotNew");
		configuration.objectClass(PilotNew.class).objectField("name").rename("identity");
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		container.close();
	}
	// end changeClass
	
	private static void retrievePilotNew(){
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.query(PilotNew.class);
			listResult(result);
		} finally {
			container.close();
		}
	}
	// end retrievePilotNew
	
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
