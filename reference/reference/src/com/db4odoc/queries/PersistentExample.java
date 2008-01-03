/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.queries;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class PersistentExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		new File(DB4O_FILE_NAME).delete();
		accessDb4o();
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			storeFirstPilot(container);
			storeSecondPilot(container);
			retrieveAllPilots(container);
			retrievePilotByName(container);
			retrievePilotByExactPoints(container);
			updatePilot(container);
			deleteFirstPilotByName(container);
			deleteSecondPilotByName(container);
		} finally {
			container.close();
		}
	}
	// end main

	private static void accessDb4o() {
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			// do something with db4o
		} finally {
			container.close();
		}
	}
	// end accessDb4o

	private static void storeFirstPilot(ObjectContainer container) {
		Pilot pilot1 = new Pilot("Michael Schumacher", 100);
		container.store(pilot1);
		System.out.println("Stored " + pilot1);
	}
	// end storeFirstPilot

	private static void storeSecondPilot(ObjectContainer container) {
		Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
		container.store(pilot2);
		System.out.println("Stored " + pilot2);
	}
	// end storeSecondPilot

	private static void retrieveAllPilotQBE(ObjectContainer container) {
		Pilot proto = new Pilot(null, 0);
		ObjectSet result = container.queryByExample(proto);
		listResult(result);
	}
	// end retrieveAllPilotQBE

	private static void retrieveAllPilots(ObjectContainer container) {
		ObjectSet result = container.queryByExample(Pilot.class);
		listResult(result);
	}
	// end retrieveAllPilots

	private static void retrievePilotByName(ObjectContainer container) {
		Pilot proto = new Pilot("Michael Schumacher", 0);
		ObjectSet result = container.queryByExample(proto);
		listResult(result);
	}
	// end retrievePilotByName

	private static void retrievePilotByExactPoints(
			ObjectContainer container) {
		Pilot proto = new Pilot(null, 100);
		ObjectSet result = container.queryByExample(proto);
		listResult(result);
	}
	// end retrievePilotByExactPoints

	private static void updatePilot(ObjectContainer container) {
		ObjectSet result = container.queryByExample(new Pilot(
				"Michael Schumacher", 0));
		Pilot found = (Pilot) result.next();
		found.addPoints(11);
		container.store(found);
		System.out.println("Added 11 points for " + found);
		retrieveAllPilots(container);
	}
	// end updatePilot

	private static void deleteFirstPilotByName(
			ObjectContainer container) {
		ObjectSet result = container.queryByExample(new Pilot(
				"Michael Schumacher", 0));
		Pilot found = (Pilot) result.next();
		container.delete(found);
		System.out.println("Deleted " + found);
		retrieveAllPilots(container);
	}
	// end deleteFirstPilotByName

	private static void deleteSecondPilotByName(
			ObjectContainer container) {
		ObjectSet result = container.queryByExample(new Pilot(
				"Rubens Barrichello", 0));
		Pilot found = (Pilot) result.next();
		container.delete(found);
		System.out.println("Deleted " + found);
		retrieveAllPilots(container);
	}
	// end deleteSecondPilotByName

	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult
}
