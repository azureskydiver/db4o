/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.Opening;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;

public class FileOpenExample {
	private final static String DB4O_FILE_NAME="reference.db4o";

	public static void main(String[] args) {
		setObjects();
		getCars();
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
	
	private static void getCars() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.readOnly(true);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			ObjectSet result = container.query(Car.class);
			listResult(result);
			getPilots();
		} finally {
			container.close();
		}
	}	
	// end getCars
	
	private static void getPilots() {
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.query(Pilot.class);
			listResult(result);
		} finally {
			container.close();
		}
	}	
	// end getPilots
	
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
