/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.tamigrate;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ta.*;

public class TAExample {

	private final static String FIRST_DB_NAME = "reference.db4o";
	private final static String SECOND_DB_NAME = "migrate.db4o";

	
	public static void main(String[] args) {
		testSwitchDatabases();
		//testSwitchDatabasesFixed();
	}

	// end main

	private static void storeSensorPanel() {
		new File(FIRST_DB_NAME).delete();
		ObjectContainer container = Db4o.openFile(FIRST_DB_NAME);
		if (container != null) {
		try {
			// create a linked list with length 10
			SensorPanelTA list = new SensorPanelTA().createList(10);
			container.store(list);
		} finally {
			container.close();
		}
		}
	}

	// end storeSensorPanel


	private static Configuration configureTA() {
		Configuration configuration = Db4o.newConfiguration();
		// add TA support
		configuration.add(new TransparentActivationSupport());
		return configuration;	
	}
	// end configureTA
	
	private static void testSwitchDatabases() {
		storeSensorPanel();

		ObjectContainer firstDb = Db4o.openFile(configureTA(), FIRST_DB_NAME);
		ObjectContainer secondDb = Db4o.openFile(configureTA(), SECOND_DB_NAME);
		try {
			ObjectSet result = firstDb.queryByExample(new SensorPanelTA(1));
			if (result.size() > 0) {
				SensorPanelTA sensor = (SensorPanelTA) result.get(0);
				firstDb.close();
				// Migrating an object from the first database
				// into a second database
				secondDb.store(sensor);
			}
		} finally {
			firstDb.close();
			secondDb.close();
		}
	}
	// end testSwitchDatabases
	

	private static void testSwitchDatabasesFixed() {
		storeSensorPanel();

		ObjectContainer firstDb = Db4o.openFile(configureTA(), FIRST_DB_NAME);
		ObjectContainer secondDb = Db4o.openFile(configureTA(), SECOND_DB_NAME);
		try {
			ObjectSet result = firstDb.queryByExample(new SensorPanelTA(1));
			if (result.size() > 0) {
				SensorPanelTA sensor = (SensorPanelTA) result.get(0);
				// Unbind the object from the first database
				sensor.bind(null);
				// Migrating the object into the second database
				secondDb.store(sensor);
				
				
				System.out.println("Retrieving previous query results from " 
						+ FIRST_DB_NAME + ":");
				SensorPanelTA next = sensor.getNext();
				while (next != null) {
					System.out.println(next);
					next = next.getNext();
				}
				
				System.out.println("Retrieving previous query results from " 
						+ FIRST_DB_NAME + " with manual activation:");
				firstDb.activate(sensor, Integer.MAX_VALUE);
				next = sensor.getNext();
				while (next != null) {
					System.out.println(next);
					next = next.getNext();
				}
				
				System.out.println("Retrieving sensorPanel from " + SECOND_DB_NAME + ":");
				result = secondDb.queryByExample(new SensorPanelTA(1));
				next = sensor.getNext();
				while (next != null) {
					System.out.println(next);
					next = next.getNext();
				}
			}
		} finally {
			firstDb.close();
			secondDb.close();
		}
	}
	// end testSwitchDatabasesFixed

	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult

}
