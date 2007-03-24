/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4odoc.marshal;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class CustomMarshallerExample  {

	private static final String DBFILE = "test.db";
	private static ItemMarshaller marshaller = null;
	
	public static void main(String[] args) {
		// store objects using standard mashaller
		storeObjects();
		// retrieve objects using standard marshaller
		retrieveObjects();
		// store and retrieve objects using the customized Item class marshaller
		configureMarshaller();
		storeObjects();
		retrieveObjects();
	}
	// end main
	
	private static void configureMarshaller(){
		marshaller = new ItemMarshaller();
		Db4o.configure().objectClass(Item.class).marshallWith(marshaller);
	}
	// end configureMarshaller
	
	private static void storeObjects(){
		new File(DBFILE).delete();
		ObjectContainer container = Db4o.openFile(DBFILE);
		try {
			Item item;
			long t1 = System.currentTimeMillis();
			for (int i = 0; i < 10000; i++){
				item = new Item(0xFFAF, 0xFFFFFFF, 120);
				container.set(item);
			}
			long t2 = System.currentTimeMillis();
			long  timeElapsed = t2 - t1;
			System.out.println("Time to store the objects ="+ timeElapsed + " ms");
		} finally {
			container.close();
		}
	}
	// end storeObjects
		
	private static void retrieveObjects(){
		ObjectContainer container = Db4o.openFile(DBFILE);
		try {
			long t1 = System.currentTimeMillis();
			ObjectSet result = container.get(new Item());
			long t2 = System.currentTimeMillis();
			long  timeElapsed = t2 - t1;
			System.out.println("Time elapsed for the query ="+ timeElapsed + " ms");
			listResult(result);
		} finally {
			container.close();
		}
	}
	// end retrieveObjects
		
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        // print only the first result
        if (result.hasNext())
        	System.out.println(result.next());
    }
    // end listResult
}
