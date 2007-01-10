/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.identity;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

public class IdentityExample {
	public final static String YAPFILENAME="formula1.yap";

	public static void main(String[] args) {
		setObjects();
		checkUniqueness();
		checkReferenceCache();
		checkReferenceCacheWithPurge();
		testBind();
		
		testCopyingWithPurge();
	}
	// end main

	public static void setObjects(){
		new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
			db.set(car);
			car = new Car("Ferrari", new Pilot("Michael Schumacher"));
			db.set(car);
		} finally {
			db.close();
		}
	}
	// end setObjects
	
	public static void checkUniqueness(){
		setObjects();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet cars = db.query(Car.class);
			Car car = (Car)cars.get(0);
			String pilotName = car.getPilot().getName();
			ObjectSet pilots = db.get(new Pilot(pilotName));
			Pilot pilot = (Pilot)pilots.get(0);
			System.out.println("Retrieved objects are identical: " + (pilot == car.getPilot()));
		} finally {
			db.close();
		}
	}
	// end checkUniqueness
	
	public static void checkReferenceCache(){
		setObjects();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet pilots = db.query(Pilot.class);
			Pilot pilot = (Pilot)pilots.get(0);
			String pilotName = pilot.getName();
			pilot.setName("new name");
			System.out.println("Retrieving pilot by name: " + pilotName);
			ObjectSet pilots1 = db.get(new Pilot(pilotName));
			listResult(pilots1);
		} finally {
			db.close();
		}
	}
	// end checkReferenceCache
	
	public static void checkReferenceCacheWithPurge(){
		setObjects();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet pilots = db.query(Pilot.class);
			Pilot pilot = (Pilot)pilots.get(0);
			String pilotName = pilot.getName();
			pilot.setName("new name");
			System.out.println("Retrieving pilot by name: " + pilotName);
			long pilotID = db.ext().getID(pilot);
			if (db.ext().isCached(pilotID)){
				db.ext().purge(pilot);
			}
			ObjectSet pilots1 = db.get(new Pilot(pilotName));
			listResult(pilots1);
		} finally {
			db.close();
		}
	}
	// end checkReferenceCacheWithPurge
	
	public static void testCopyingWithPurge(){
		setObjects();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet pilots = db.query(Pilot.class);
			Pilot pilot = (Pilot)pilots.get(0);
			db.ext().purge(pilot);
			db.set(pilot);
			pilots = db.query(Pilot.class);
			listResult(pilots);
		} finally {
			db.close();
		}
	}
	// end testCopyingWithPurge
	
	public static void testBind(){
		setObjects();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Query q = db.query();
			q.constrain(Car.class);
			q.descend("model").constrain("Ferrari");
			ObjectSet result = q.execute();
			Car car1 = (Car)result.get(0);
			long IdCar1 = db.ext().getID(car1);
			Car car2 = new Car("BMW", new Pilot("Rubens Barrichello"));
			db.ext().bind(car2,IdCar1);
			db.set(car2);

			result = db.query(Car.class);
			listResult(result);
		} finally {
			db.close();
		}
	}
	// end testBind
	
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
