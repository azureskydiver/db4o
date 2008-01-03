/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.callbacks;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.events.CancellableObjectEventArgs;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.events.ObjectEventArgs;
import com.db4o.query.Query;


public class CallbacksExample {

	private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void main(String[] args) {
		testCreated();
		testCascadedDelete();
		testIntegrityCheck();
	}
	// end main

	private static void testCreated(){
		new File(DB4O_FILE_NAME).delete();
		ObjectServer server = Db4o.openServer(DB4O_FILE_NAME, 0);
		ObjectContainer container = server.openClient();
		//ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			EventRegistry registry =  EventRegistryFactory.forObjectContainer(container);
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
			container.store(car);
		} finally {
			container.close();
			server.close();
		}
	}
	// end testCreated
	
	private static void fillDB(){
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Car car = new Car("BMW",new Pilot("Rubens Barrichello"));
			container.store(car);
			car = new Car("Ferrari",new Pilot("Kimi Raikkonen"));
			container.store(car);
		} finally {
			container.close();
		}
	}
	// end fillDB
	
	private static void testCascadedDelete(){
		fillDB();
		Configuration con = Db4o.newConfiguration();
		con.callbacks(false);
		ObjectServer server = Db4o.openServer(con, DB4O_FILE_NAME, 0xdb40);
		server.grantAccess("A", "A");
		final ObjectContainer container = Db4o.openClient(con, "localhost", 0xdb40, "A", "A");
		
		//final ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			// check the contents of the database
			ObjectSet result = container.queryByExample(null);
			listResult(result);
			
			EventRegistry registry =  EventRegistryFactory.forObjectContainer(container);
			// register an event handler, which will delete the pilot when his car is deleted 
			registry.deleted().addListener(new EventListener4() {
				public void onEvent(Event4 e, EventArgs args) {
					ObjectEventArgs queryArgs = ((ObjectEventArgs) args);
					Object obj = queryArgs.object();
					if (obj instanceof Car){
						container.delete(((Car)obj).getPilot());
					}
				}
			});
			// delete all the cars
			result = container.query(Car.class);
			while(result.hasNext()) {
	            container.delete(result.next());
	        }
			// check if the database is empty
			result = container.queryByExample(null);
			listResult(result);
		} finally {
			container.close();
			server.close();
		}
	}
	// end testCascadedDelete
	
	private static void testIntegrityCheck(){
		fillDB();
		final ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			EventRegistry registry =  EventRegistryFactory.forObjectContainer(container);
			// register an event handler, which will stop deleting a pilot when it is referenced from a car 
			registry.deleting().addListener(new EventListener4() {
				public void onEvent(Event4 e, EventArgs args) {
					CancellableObjectEventArgs cancellableArgs = ((CancellableObjectEventArgs) args);
					Object obj = cancellableArgs.object();
					if (obj instanceof Pilot){
						Query q = container.query();
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
			ObjectSet result = container.queryByExample(null);
			listResult(result);
			
			// try to delete all the pilots
			result = container.query(Pilot.class);
			while(result.hasNext()) {
	            container.delete(result.next());
	        }
			// check if any of the objects were deleted
			result = container.queryByExample(null);
			listResult(result);
		} finally {
			container.close();
		}
	}
	// end testIntegrityCheck
	
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
