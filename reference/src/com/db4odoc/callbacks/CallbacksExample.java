/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.callbacks;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.events.CancellableObjectEventArgs;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.events.ObjectEventArgs;
import com.db4o.query.Query;


public class CallbacksExample {

	private final static String YAPFILENAME="formula1.yap";
	
	public static void main(String[] args) {
		testCreated();
		testCascadedDelete();
		testIntegrityCheck();
	}
	// end main

	public static void testCreated(){
		new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			EventRegistry registry =  EventRegistryFactory.forObjectContainer(db);
			// register an event handler, which will print all the car objects, that have been created
			registry.created().addListener(new EventListener4() {
				public void onEvent(Event4 e, EventArgs args) {
					ObjectEventArgs queryArgs = ((ObjectEventArgs) args);
					Object obj = queryArgs.object();
					if (obj instanceof Pilot){
						System.out.println(obj.toString());
					}
				}
			});

			Car car = new Car("BMW",new Pilot("Rubens Barrichello"));
			db.set(car);
		} finally {
			db.close();
		}
	}
	// end testCreated
	
	public static void fillDB(){
		new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Car car = new Car("BMW",new Pilot("Rubens Barrichello"));
			db.set(car);
			car = new Car("Ferrari",new Pilot("Finn Kimi Raikkonen"));
			db.set(car);
		} finally {
			db.close();
		}
	}
	// end fillDB
	
	public static void testCascadedDelete(){
		fillDB();
		final ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			// check the contents of the database
			ObjectSet result = db.get(null);
			listResult(result);
			
			EventRegistry registry =  EventRegistryFactory.forObjectContainer(db);
			// register an event handler, which will delete the pilot when his car is deleted 
			registry.deleted().addListener(new EventListener4() {
				public void onEvent(Event4 e, EventArgs args) {
					ObjectEventArgs queryArgs = ((ObjectEventArgs) args);
					Object obj = queryArgs.object();
					if (obj instanceof Car){
						db.delete(((Car)obj).getPilot());
					}
				}
			});
			// delete all the cars
			result = db.query(Car.class);
			while(result.hasNext()) {
	            db.delete(result.next());
	        }
			// check if the database is empty
			result = db.get(null);
			listResult(result);
		} finally {
			db.close();
		}
	}
	// end testCascadedDelete
	
	public static void testIntegrityCheck(){
		fillDB();
		final ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			EventRegistry registry =  EventRegistryFactory.forObjectContainer(db);
			// register an event handler, which will stop deleting a pilot when it is referenced from a car 
			registry.deleting().addListener(new EventListener4() {
				public void onEvent(Event4 e, EventArgs args) {
					CancellableObjectEventArgs cancellableArgs = ((CancellableObjectEventArgs) args);
					Object obj = cancellableArgs.object();
					if (obj instanceof Pilot){
						Query q = db.query();
						q.constrain(Car.class);
						q.descend("pilot").constrain(obj);
						ObjectSet result = q.execute();
						if (result.size() > 0) {
							System.out.println("Object " +  (Pilot)obj + " can't be deleted as object container has references to it");
							cancellableArgs.cancel();
						}
					}
				}
			});
			
			// check the contents of the database
			ObjectSet result = db.get(null);
			listResult(result);
			
			// try to delete all the pilots
			result = db.query(Pilot.class);
			while(result.hasNext()) {
	            db.delete(result.next());
	        }
			// check if any of the objects were deleted
			result = db.get(null);
			listResult(result);
		} finally {
			db.close();
		}
	}
	// end testIntegrityCheck
	
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
