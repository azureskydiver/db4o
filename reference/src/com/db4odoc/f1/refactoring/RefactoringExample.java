/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.refactoring;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4odoc.f1.Util;


public class RefactoringExample extends Util {

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

	public static void setObjects(){
		new File(Util.YAPFILENAME).delete();
		ObjectContainer oc = Db4o.openFile(Util.YAPFILENAME);
		try {
			Pilot pilot = new Pilot("Rubens Barrichello");
			oc.set(pilot);
			pilot = new Pilot("Michael Schumacher");
			oc.set(pilot);
		} finally {
			oc.close();
		}
	}
	
	public static void setNewObjects(){
		ObjectContainer oc = Db4o.openFile(Util.YAPFILENAME);
		try {
			PilotNew pilot = new PilotNew("Rubens Barrichello",99);
			oc.set(pilot);
			pilot = new PilotNew("Michael Schumacher",100);
			oc.set(pilot);
		} finally {
			oc.close();
		}
	}
	
	public static void checkDB(){
		ObjectContainer oc = Db4o.openFile(Util.YAPFILENAME);
		try {
			retrieveAll(oc);
		} finally {
			oc.close();
		}
	}
	
	public static void changeClass(){
		Db4o.configure().objectClass(Pilot.class).rename("com.db4odoc.f1.refactoring.PilotNew");
		Db4o.configure().objectClass(PilotNew.class).objectField("name").rename("identity");
		ObjectContainer oc = Db4o.openFile(Util.YAPFILENAME);
		oc.close();
	}
	
	public static void retrievePilotNew(){
		ObjectContainer oc = Db4o.openFile(Util.YAPFILENAME);
		try {
			ObjectSet result = oc.query(PilotNew.class);
			listResult(result);
		} finally {
			oc.close();
		}
	}
}
