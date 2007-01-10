/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
/*
 * This example shows how to implement object callbacks to assign 
 * autoincremented ID to a special type of objects
 */
package com.db4odoc.callbacks;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.events.ObjectEventArgs;


public class AutoIncExample {

	private final static String YAPFILENAME="formula1.yap";
	
	public static void main(String[] args) {
		
		ObjectContainer db=null;
		
		new File (YAPFILENAME).delete();
		try{
			db = Db4o.openFile(YAPFILENAME);
			registerCallback(db);
			storeObjects(db);
				
			retrieveObjects(db);
		} finally {
			db.close();
		}
	}
	// end main
	
	public static void retrieveObjects(ObjectContainer db){
		ObjectSet result = db.get(new TestObject(null));
		listResult(result);
	}
	// end retrieveObjects
	
	public static void storeObjects(ObjectContainer db){
		TestObject test;
		test = new TestObject("FirstObject");
		db.set(test);
		test = new TestObject("SecondObject");
		db.set(test);
		test = new TestObject("ThirdObject");
		db.set(test);
	}
	// end storeObjects
	
	public static void registerCallback(final ObjectContainer db){
		EventRegistry registry =  EventRegistryFactory.forObjectContainer(db);
		// register an event handler, which will assign autoincremented IDs to any
		// object extending CountedObject, when the object is created
		registry.creating().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				ObjectEventArgs queryArgs = ((ObjectEventArgs) args);
				Object obj = queryArgs.object();
				// only for the objects extending the CountedObject
				if (obj instanceof CountedObject){
					((CountedObject)obj).setId(getNextId(db));
				}
			}
		});
	}
	// end registerCallback

	private static int getNextId(ObjectContainer db) {
		// this function retrieves the next available ID from 
		// the IncrementedId object
		IncrementedId r = IncrementedId.getIdObject(db); 	
		int nRoll;
		nRoll = r.getNextID(db);
		
		return nRoll;
	}
	// end getNextId
	
	public static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while(result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult
}
