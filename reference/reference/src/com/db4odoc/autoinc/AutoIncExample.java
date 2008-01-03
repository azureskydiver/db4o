/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
/*
 * This example shows how to implement object callbacks to assign 
 * autoincremented ID to a special type of objects
 */
package com.db4odoc.autoinc;

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

	private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void main(String[] args) {
		
		ObjectContainer container=null;
		
		new File (DB4O_FILE_NAME).delete();
		try{
			container = Db4o.openFile(DB4O_FILE_NAME);
			registerCallback(container);
			storeObjects(container);
				
			retrieveObjects(container);
		} finally {
			container.close();
		}
	}
	// end main
	
	private static void retrieveObjects(ObjectContainer container){
		ObjectSet result = container.queryByExample(new TestObject(null));
		listResult(result);
	}
	// end retrieveObjects
	
	private static void storeObjects(ObjectContainer container){
		TestObject test;
		test = new TestObject("FirstObject");
		container.store(test);
		test = new TestObject("SecondObject");
		container.store(test);
		test = new TestObject("ThirdObject");
		container.store(test);
	}
	// end storeObjects
	
	private static void registerCallback(final ObjectContainer container){
		EventRegistry registry =  EventRegistryFactory.forObjectContainer(container);
		// register an event handler, which will assign autoincremented IDs to any
		// object extending CountedObject, when the object is created
		registry.creating().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				ObjectEventArgs queryArgs = ((ObjectEventArgs) args);
				Object obj = queryArgs.object();
				// only for the objects extending the CountedObject
				if (obj instanceof CountedObject){
					((CountedObject)obj).setId(getNextId(container));
				}
			}
		});
	}
	// end registerCallback

	private static int getNextId(ObjectContainer container) {
		// this function retrieves the next available ID from 
		// the IncrementedId object
		IncrementedId r = IncrementedId.getIdObject(container); 	
		int nRoll;
		nRoll = r.getNextID(container);
		
		return nRoll;
	}
	// end getNextId
	
	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while(result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult
}
