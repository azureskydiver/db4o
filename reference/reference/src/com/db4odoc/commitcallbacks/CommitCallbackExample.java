/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.commitcallbacks;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.events.CommitEventArgs;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.ext.Db4oException;
import com.db4o.ext.ObjectInfo;
import com.db4o.ext.ObjectInfoCollection;
import com.db4o.foundation.Iterator4;


public class CommitCallbackExample {
	
	private static final String FILENAME = "reference.db4o";
	private static ObjectContainer _container;
	
	public static void main(String[] args) {
		new File(FILENAME).delete();
		try
		{
			configure();
			storeFirstObject();
			storeOtherObjects();
		} finally {
			container().close();
		}
	}
	// end main

	private static ObjectContainer container(){
		if (_container == null){
			_container = Db4o.openFile(FILENAME);
		} 
		return _container;
	}
	// end container
	
	private static void configure(){
		EventRegistry registry =  EventRegistryFactory.forObjectContainer(container());
		// register an event handler, which will check object uniqueness on commit
		registry.committing().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				CommitEventArgs commitArgs = ((CommitEventArgs) args);
				// uniqueness should be checked for both added and updated objects
				checkUniqueness(commitArgs.added());
				checkUniqueness(commitArgs.updated());
			}
		});
	}
	// end configure
	
	private static void checkUniqueness(ObjectInfoCollection collection){
		Iterator4 iterator = collection.iterator();
		while (iterator.moveNext()){
			ObjectInfo info = (ObjectInfo)iterator.current();
			// only check for Item objects
			if (info.getObject() instanceof Item){
				Item item  = (Item)info.getObject();
				// search for objects with the same fields in the database
				ObjectSet found = container().get(new Item(item.getNumber(), item.getWord()));
				if (found.size() > 1){
					throw new Db4oException("Object is not unique: " + item);
				}
			}
		}
	}
	// end checkUniqueness
	
	private static void storeFirstObject(){
		ObjectContainer container = container();
		try {
			// creating and storing item1 to the database
			Item item = new Item(1, "one");
			container.set(item);
			// no problems here
			container.commit();
		} catch (Db4oException ex){
			System.out.println(ex.getMessage());
			container.rollback();
		} 
	}
	// end storeFirstObject
	
	private static void storeOtherObjects(){
		ObjectContainer container = container();
		// creating and storing similar items to the database
		Item item = new Item(2, "one");
		container.set(item);
		item = new Item(1, "two");
		container.set(item);
		try {
			// commit should work as there were no duplicate objects
			container.commit();
		} catch (Db4oException ex){
			System.out.println(ex.getMessage());
			container.rollback();
		}
		System.out.println("Commit successful");
		
		// trying to save a duplicate object to the database
		item = new Item(1, "one");
		container.set(item);
		try {
			// Commit should fail as duplicates are not allowed
			container.commit();
		} catch (Db4oException ex){
			System.out.println(ex.getMessage());
			container.rollback();
		}
	}
	// end storeOtherObjects
}
