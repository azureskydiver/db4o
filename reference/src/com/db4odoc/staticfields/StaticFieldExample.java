/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.staticfields;

import java.awt.Color;
import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;

public class StaticFieldExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		setPilotsSimple();
		checkPilots();
		checkDatabaseFileSize();
		//
		setPilotsStatic();
		checkPilots();
		checkDatabaseFileSize();
		updatePilots();
		updatePilotCategories();
		checkPilots();
		deleteTest();
	}

	// end main

	private static void setCar() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Car car = new Car();
			car.color = Color.GREEN;
			container.set(car);
		} finally {
			container.close();
		}
	}

	// end setCar

	private static void setPilotsSimple() {
		System.out
				.println("In the default setting, static constants are not continously stored and updated.");
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			container.set(new Pilot("Michael Schumacher",
					PilotCategories.WINNER));
			container.set(new Pilot("Rubens Barrichello",
					PilotCategories.TALENTED));
		} finally {
			container.close();
		}
	}

	// end setPilotsSimple

	private static void setPilotsStatic() {
		System.out.println("The feature can be turned on for individual classes.");
		new File(DB4O_FILE_NAME).delete();
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(
				"com.db4odoc.f1.staticfields.PilotCategories")
				.persistStaticFieldValues();
		
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			container.set(new Pilot("Michael Schumacher",
					PilotCategories.WINNER));
			container.set(new Pilot("Rubens Barrichello",
					PilotCategories.TALENTED));
		} finally {
			container.close();
		}
	}

	// end setPilotsStatic

	private static void checkPilots() {
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.query(Pilot.class);
			for (int x = 0; x < result.size(); x++) {
				Pilot pilot = (Pilot) result.get(x);
				if (pilot.getCategory() == PilotCategories.WINNER) {
					System.out.println("Winner pilot: " + pilot);
				} else if (pilot.getCategory() == PilotCategories.TALENTED) {
					System.out.println("Talented pilot: " + pilot);
				} else {
					System.out.println("Uncategorized pilot: "
							+ pilot);
				}
			}
		} finally {
			container.close();
		}
	}

	// end checkPilots

	private static void updatePilots() {
		System.out
				.println("Updating PilotCategory in pilot reference:");
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.query(Pilot.class);
			for (int x = 0; x < result.size(); x++) {
				Pilot pilot = (Pilot) result.get(x);
				if (pilot.getCategory() == PilotCategories.WINNER) {
					System.out.println("Winner pilot: " + pilot);
					PilotCategories pc = pilot.getCategory();
					pc.testChange("WINNER2006");
					container.set(pilot);
				}
			}
			printCategories(container);
		} finally {
			container.close();
		}
	}

	// end updatePilots

	private static void updatePilotCategories() {
		System.out.println("Updating PilotCategories explicitly:");
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.query(PilotCategories.class);
			for (int x = 0; x < result.size(); x++) {
				PilotCategories pc = (PilotCategories) result.get(x);
				if (pc == PilotCategories.WINNER) {
					pc.testChange("WINNER2006");
					container.set(pc);
				}
			}
			printCategories(container);
		} finally {
			container.close();
		}
		System.out.println("Change the value back:");
		container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.query(PilotCategories.class);
			for (int x = 0; x < result.size(); x++) {
				PilotCategories pc = (PilotCategories) result.get(x);
				if (pc == PilotCategories.WINNER) {
					pc.testChange("WINNER");
					container.set(pc);
				}
			}
			printCategories(container);
		} finally {
			container.close();
		}
	}

	// end updatePilotCategories

	private static void deleteTest() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(Pilot.class).cascadeOnDelete(true);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
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
			container.close();
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
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			printCategories(container);
			ObjectSet result = container.query(PilotCategories.class);
			for (int x = 0; x < result.size(); x++) {
				PilotCategories pc = (PilotCategories) result.get(x);
				container.delete(pc);
			}
			printCategories(container);
		} finally {
			container.close();
		}
	}

	// end deletePilotCategories

	private static void checkDatabaseFileSize() {
		System.out.println("Database file size: "
				+ new File(DB4O_FILE_NAME).length() + "\n");
	}
	// end checkDatabaseFileSize
}
