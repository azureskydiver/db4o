/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.identity;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

public class IdentityExample {
	private final static String DB4O_FILE_NAME="reference.db4o";

	public static void main(String[] args) {
		setObjects();
		//checkUniqueness();
		//checkReferenceCache();
		//checkReferenceCacheWithPurge();
		testBind();
		
		testCopyingWithPurge();
	}
	// end main

	private static void setObjects(){
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
			container.store(car);
			car = new Car("Ferrari", new Pilot("Michael Schumacher"));
			container.store(car);
		} finally {
			container.close();
		}
	}
	// end setObjects
	
	private static void checkUniqueness(){
		setObjects();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet cars = container.query(Car.class);
			Car car = (Car)cars.get(0);
			String pilotName = car.getPilot().getName();
			ObjectSet pilots = container.queryByExample(new Pilot(pilotName));
			Pilot pilot = (Pilot)pilots.get(0);
			System.out.println("Retrieved objects are identical: " + (pilot == car.getPilot()));
		} finally {
			container.close();
		}
	}
	// end checkUniqueness
	
	private static void checkReferenceCache(){
		setObjects();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet pilots = container.query(Pilot.class);
			Pilot pilot = (Pilot)pilots.get(0);
			String pilotName = pilot.getName();
			pilot.setName("new name");
			System.out.println("Retrieving pilot by name: " + pilotName);
			ObjectSet pilots1 = container.queryByExample(new Pilot(pilotName));
			listResult(pilots1);
		} finally {
			container.close();
		}
	}
	// end checkReferenceCache
	
	private static void checkReferenceCacheWithPurge(){
		setObjects();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet pilots = container.query(Pilot.class);
			Pilot pilot = (Pilot)pilots.get(0);
			String pilotName = pilot.getName();
			pilot.setName("new name");
			System.out.println("Retrieving pilot by name: " + pilotName);
			long pilotID = container.ext().getID(pilot);
			if (container.ext().isCached(pilotID)){
				container.ext().purge(pilot);
			}
			ObjectSet pilots1 = container.queryByExample(new Pilot(pilotName));
			listResult(pilots1);
		} finally {
			container.close();
		}
	}
	// end checkReferenceCacheWithPurge
	
	private static void testCopyingWithPurge(){
		setObjects();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet pilots = container.query(Pilot.class);
			Pilot pilot = (Pilot)pilots.get(0);
			container.ext().purge(pilot);
			container.store(pilot);
			pilots = container.query(Pilot.class);
			listResult(pilots);
		} finally {
			container.close();
		}
	}
	// end testCopyingWithPurge
	
	private static void testBind(){
		setObjects();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Query q = container.query();
			q.constrain(Car.class);
			q.descend("model").constrain("Ferrari");
			ObjectSet result = q.execute();
			Car car1 = (Car)result.get(0);
			long IdCar1 = container.ext().getID(car1);
			Car car2 = new Car("BMW", new Pilot("Rubens Barrichello"));
			container.ext().bind(car2,289);
			container.store(car2);

			result = container.query(Car.class);
			listResult(result);
		} finally {
			container.close();
		}
	}
	// end testBind
	
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
