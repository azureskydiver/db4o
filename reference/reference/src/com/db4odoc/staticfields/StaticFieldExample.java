/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.staticfields;

import java.awt.*;
import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

public class StaticFieldExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";
	
	private static ObjectContainer _container = null;
	private static Configuration _configuration = null;

	public static void main(String[] args) {
		System.out
		.println("In the default setting, static constants are not continously stored and updated.");

		setPilots();
		checkPilots();
		checkDatabaseFileSize();
		//
		configure();
		setPilots();
		checkPilots();
		checkDatabaseFileSize();
		updatePilots();
		updatePilotCategories();
		checkPilots();
		addDeleteConfiguration();
		deleteTest();
	}

	// end main

	private static void setCar() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Car car = new Car();
			car.color = Color.GREEN;
			container.store(car);
		} finally {
			container.close();
		}
	}

	// end setCar

	private static ObjectContainer database() {
		if (_container == null) {
			try {
				if (_configuration == null) { 
					_container = Db4o.openFile(DB4O_FILE_NAME);
				} else {
					_container = Db4o.openFile(_configuration, DB4O_FILE_NAME);
				}
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

	private static void configure(){
		System.out.println("Saving static fields can be turned on for individual classes.");
		
		_configuration = Db4o.newConfiguration();
		_configuration.objectClass(PilotCategories.class)
				.persistStaticFieldValues();
	}
	// end configure
	
	private static void setPilots() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database();
		if (container != null) {
			try {
				container.store(new Pilot("Michael Schumacher",
						PilotCategories.WINNER));
				container.store(new Pilot("Rubens Barrichello",
						PilotCategories.TALENTED));
			} finally {
				closeDatabase();
			}
		}
	}

	// end setPilots

	
	private static void checkPilots() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				ObjectSet result = container.query(Pilot.class);
				for (int x = 0; x < result.size(); x++) {
					Pilot pilot = (Pilot) result.get(x);
					if (pilot.getCategory() == PilotCategories.WINNER) {
						System.out.println("Winner pilot: " + pilot);
					} else if (pilot.getCategory() == PilotCategories.TALENTED) {
						System.out.println("Talented pilot: " + pilot);
					} else {
						System.out.println("Uncategorized pilot: " + pilot);
					}
				}
			} finally {
				closeDatabase();
			}
		}
	}

	// end checkPilots

	
	private static void updatePilots() {
		System.out
				.println("Updating PilotCategory in pilot reference:");
		ObjectContainer container = database();
		if (container != null){
			try {
				ObjectSet result = container.query(Pilot.class);
				for (int x = 0; x < result.size(); x++) {
					Pilot pilot = (Pilot) result.get(x);
					if (pilot.getCategory() == PilotCategories.WINNER) {
						System.out.println("Winner pilot: " + pilot);
						PilotCategories pc = pilot.getCategory();
						pc.testChange("WINNER2006");
						container.store(pilot);
					}
				}
				printCategories(container);
			} finally {
				closeDatabase();
			}
		}
	}

	// end updatePilots

	private static void updatePilotCategories() {
		System.out.println("Updating PilotCategories explicitly:");
		ObjectContainer container = database();
		if (container != null){
			try {
				ObjectSet result = container.query(PilotCategories.class);
				for (int x = 0; x < result.size(); x++) {
					PilotCategories pc = (PilotCategories) result.get(x);
					if (pc == PilotCategories.WINNER) {
						pc.testChange("WINNER2006");
						container.store(pc);
					}
				}
				printCategories(container);
			} finally {
				closeDatabase();
			}
		}
		System.out.println("Change the value back:");
		container = database();
		if (container != null){
			try {
				ObjectSet result = container.query(PilotCategories.class);
				for (int x = 0; x < result.size(); x++) {
					PilotCategories pc = (PilotCategories) result.get(x);
					if (pc == PilotCategories.WINNER) {
						pc.testChange("WINNER");
						container.store(pc);
					}
				}
				printCategories(container);
			} finally {
				closeDatabase();
			}
		}
	}

	// end updatePilotCategories

	private static void addDeleteConfiguration(){
		if (_configuration != null){
			_configuration.objectClass(Pilot.class).cascadeOnDelete(true);
		}
	}
	// end addDeleteConfiguration
	
	private static void deleteTest() {
		// use delete configuration
		ObjectContainer container = database();
		if (container != null){
			try {
				System.out.println("Deleting Pilots :");
				ObjectSet result = container.query(Pilot.class);
				for (int x = 0; x < result.size(); x++) {
					Pilot pilot = (Pilot) result.get(x);
					container.delete(pilot);
				}
				printCategories(container);
				System.out.println("Deleting PilotCategories :");
				result = container.query(PilotCategories.class);
				for (int x = 0; x < result.size(); x++) {
					container.delete(result.get(x));
				}
				printCategories(container);
			} finally {
				closeDatabase();
			}
		}
	}

	// end deleteTest

	private static void printCategories(ObjectContainer container) {
		ObjectSet result = container.query(PilotCategories.class);
		System.out.println("Stored categories: " + result.size());
		for (int x = 0; x < result.size(); x++) {
			PilotCategories pc = (PilotCategories) result.get(x);
			System.out.println("Category: " + pc);
		}
	}

	// end printCategories

	private static void deletePilotCategories() {
		ObjectContainer container = database();
		if (container != null){
			try {
				printCategories(container);
				ObjectSet result = container.query(PilotCategories.class);
				for (int x = 0; x < result.size(); x++) {
					PilotCategories pc = (PilotCategories) result.get(x);
					container.delete(pc);
				}
				printCategories(container);
			} finally {
				closeDatabase();
			}
		}
	}

	// end deletePilotCategories

	private static void checkDatabaseFileSize() {
		System.out.println("Database file size: "
				+ new File(DB4O_FILE_NAME).length() + "\n");
	}
	// end checkDatabaseFileSize
}
