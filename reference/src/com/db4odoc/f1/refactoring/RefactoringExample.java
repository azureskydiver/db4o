/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.refactoring;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class RefactoringExample {
	public final static String YAPFILENAME="formula1.yap";
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

	public static void setObjects(){
		new File(YAPFILENAME).delete();
		ObjectContainer oc = Db4o.openFile(YAPFILENAME);
		try {
			Pilot pilot = new Pilot("Rubens Barrichello");
			oc.set(pilot);
			pilot = new Pilot("Michael Schumacher");
			oc.set(pilot);
		} finally {
			oc.close();
		}
	}
	// end setObjects
	
	public static void setNewObjects(){
		ObjectContainer oc = Db4o.openFile(YAPFILENAME);
		try {
			PilotNew pilot = new PilotNew("Rubens Barrichello",99);
			oc.set(pilot);
			pilot = new PilotNew("Michael Schumacher",100);
			oc.set(pilot);
		} finally {
			oc.close();
		}
	}
	// end setNewObjects
	
	public static void checkDB(){
		ObjectContainer oc = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet result=oc.get(new Object());
	        listResult(result);
		} finally {
			oc.close();
		}
	}
	// end checkDB
	
	public static void changeClass(){
		Db4o.configure().objectClass(Pilot.class).rename("com.db4odoc.f1.refactoring.PilotNew");
		Db4o.configure().objectClass(PilotNew.class).objectField("name").rename("identity");
		ObjectContainer oc = Db4o.openFile(YAPFILENAME);
		oc.close();
	}
	// end changeClass
	
	public static void retrievePilotNew(){
		ObjectContainer oc = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet result = oc.query(PilotNew.class);
			listResult(result);
		} finally {
			oc.close();
		}
	}
	// end retrievePilotNew
	
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
