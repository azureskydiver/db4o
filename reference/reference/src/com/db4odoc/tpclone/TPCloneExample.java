/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.tpclone;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.ta.*;

public class TPCloneExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private static ObjectContainer _container = null;

	public static void main(String[] args) 
	throws CloneNotSupportedException {
		storeCar();
		testClone();
	}

	// end main

	private static void storeCar() throws CloneNotSupportedException {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database(Db4o.newConfiguration());
		if (container != null) {
			try {
				// create a car
                Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
                container.store(car);
                // clone
                Car car1 = (Car)car.clone();
                container.store(car1);
			} finally {
				closeDatabase();
			}
		}
	}

	// end storeCar


	private static Configuration configureTP() {
		Configuration configuration = Db4o.newConfiguration();
		// add TP support
		configuration.add(new TransparentPersistenceSupport());
		return configuration;
	}

	// end configureTP

	private static void testClone() throws CloneNotSupportedException{
		storeCar();
		Configuration configuration = configureTP();

		ObjectContainer container = database(configuration);
		if (container != null) {
			try {
				ObjectSet result = container.queryByExample(new Car(null, null));
				listResult(result);
                Car car = null;
                Car car1 = null;
                if (result.size() > 0)
                {
                    car = (Car)result.get(0);
                    System.out.println("Retrieved car: " + car);
                    car1 = (Car)car.clone();
                    System.out.println("Storing cloned car: " + car1);
                    container.store(car1);
                }
			} finally {
				closeDatabase();
			}
		}
	}

	// end testClone


	private static ObjectContainer database(Configuration configuration) {
		if (_container == null) {
			try {
				_container = Db4o.openFile(configuration, DB4O_FILE_NAME);
			} catch (DatabaseFileLockedException ex) {
				System.out.println(ex.getMessage());
			}
		}
		return _container;
	}

	// end database

	private static void closeDatabase() {
		if (_container != null) {
			_container.close();
			_container = null;
		}
	}

	// end closeDatabase

	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult

}
