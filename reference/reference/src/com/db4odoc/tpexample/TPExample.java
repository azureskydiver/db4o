/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.tpexample;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.ta.*;

public class TPExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		testTransparentPersistence();
	}

	// end main

	private static void storeSensorPanel() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database(Db4o.newConfiguration());
		if (container != null) {
			try {
				// create a linked list with length 10
				SensorPanel list = new SensorPanel().createList(10);
				container.store(list);
			} finally {
				closeDatabase();
			}
		}
	}

	// end storeSensorPanel


	private static Configuration configureTP() {
		Configuration configuration = Db4o.newConfiguration();
		// add TP support
		configuration.add(new TransparentPersistenceSupport());
		return configuration;
	}

	// end configureTP

	private static void testTransparentPersistence() {
		storeSensorPanel();
		Configuration configuration = configureTP();

		ObjectContainer container = database(configuration);
		if (container != null) {
			try {
				ObjectSet result = container.queryByExample(new SensorPanel(1));
				listResult(result);
				SensorPanel sensor = null;
				if (result.size() > 0) {
					System.out.println("Before modification: ");
					sensor = (SensorPanel) result.get(0);
					// the object is a linked list, so each call to next()
					// will need to activate a new object
					SensorPanel next = sensor.getNext();
					while (next != null) {
						System.out.println(next);
						// modify the next sensor
						next.setSensor(new Integer(10 + (Integer)next.getSensor()));
						next = next.getNext();
					}
					// Explicit commit stores and commits the changes at any time
                    container.commit();
				}
			} finally {
				// If there are unsaved changes to activatable objects, they 
                // will be implicitly saved and committed when the database 
                // is closed
				closeDatabase();
			}
		}
		// reopen the database and check the modifications
		container = database(configuration);
		if (container != null) {
			try {
				ObjectSet result = container.queryByExample(new SensorPanel(1));
				listResult(result);
				SensorPanel sensor = null;
				if (result.size() > 0) {
					System.out.println("After modification: ");
					sensor = (SensorPanel) result.get(0);
					SensorPanel next = sensor.getNext();
					while (next != null) {
						System.out.println(next);
						next = next.getNext();
					}
				}
			} finally {
				closeDatabase();
			}
		}
	}

	// end testTransparentPersistence


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
