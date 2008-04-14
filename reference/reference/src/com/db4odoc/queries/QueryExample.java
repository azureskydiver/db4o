/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.queries;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;

public class QueryExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		storePilot();
		updatePilotWrong();
		updatePilot();
		deletePilot();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			retrievePilotByName(container);
			retrievePilotByExactPoints(container);
			retrieveByNegation(container);
			retrieveByConjunction(container);
			retrieveByDisjunction(container);
			retrieveByComparison(container);
			retrieveByDefaultFieldValue(container);
			retrieveSorted(container);
		} finally {
			container.close();
		}
	}
	// end main

	private static void storePilot() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Pilot pilot = new Pilot("Michael Schumacher", 0);
			container.store(pilot);
			System.out.println("Stored " + pilot);
			// change pilot and resave updated
			pilot.addPoints(10);
			container.store(pilot);
			System.out.println("Stored " + pilot);
		} finally {
			container.close();
		}
		retrieveAllPilots();
	}
	// end storePilot

	private static void updatePilot() {
		storePilot();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			// first retrieve the object from the database
			ObjectSet result = container.queryByExample(new Pilot(
					"Michael Schumacher", 10));
			Pilot found = (Pilot) result.next();
			found.addPoints(10);
			container.store(found);
			System.out.println("Added 10 points for " + found);
		} finally {
			container.close();
		}
		retrieveAllPilots();
	}
	// end updatePilot

	private static void updatePilotWrong() {
		storePilot();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			// Even completely identical Pilot object
			// won't work for update of the saved pilot
			Pilot pilot = new Pilot("Michael Schumacher", 10);
			pilot.addPoints(10);
			container.store(pilot);
			System.out.println("Added 10 points for " + pilot);
		} finally {
			container.close();
		}
		retrieveAllPilots();
	}
	// end updatePilotWrong

	private static void deletePilot() {
		storePilot();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			// first retrieve the object from the database
			ObjectSet result = container.queryByExample(new Pilot(
					"Michael Schumacher", 10));
			Pilot found = (Pilot) result.next();
			container.delete(found);
			System.out.println("Deleted " + found);
		} finally {
			container.close();
		}
		retrieveAllPilots();
	}
	// end deletePilot

	private static void retrieveAllPilots() {
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Query query = container.query();
			query.constrain(Pilot.class);
			ObjectSet result = query.execute();
			listResult(result);
		} finally {
			container.close();
		}
	}
	// end retrieveAllPilots

	private static void retrievePilotByName(ObjectContainer container) {
		Query query = container.query();
		query.constrain(Pilot.class);
		query.descend("name").constrain("Michael Schumacher");
		ObjectSet result = query.execute();
		listResult(result);
	}
	// end retrievePilotByName

	private static void retrievePilotByExactPoints(
			ObjectContainer container) {
		Query query = container.query();
		query.constrain(Pilot.class);
		query.descend("points").constrain(new Integer(100));
		ObjectSet result = query.execute();
		listResult(result);
	}
	// end retrievePilotByExactPoints

	private static void retrieveByNegation(ObjectContainer container) {
		Query query = container.query();
		query.constrain(Pilot.class);
		query.descend("name").constrain("Michael Schumacher").not();
		ObjectSet result = query.execute();
		listResult(result);
	}
	// end retrieveByNegation

	private static void retrieveByConjunction(ObjectContainer container) {
		Query query = container.query();
		query.constrain(Pilot.class);
		Constraint constr = query.descend("name").constrain(
				"Michael Schumacher");
		query.descend("points").constrain(new Integer(99))
				.and(constr);
		ObjectSet result = query.execute();
		listResult(result);
	}
	// end retrieveByConjunction

	private static void retrieveByDisjunction(ObjectContainer container) {
		Query query = container.query();
		query.constrain(Pilot.class);
		Constraint constr = query.descend("name").constrain(
				"Michael Schumacher");
		query.descend("points").constrain(new Integer(99)).or(constr);
		ObjectSet result = query.execute();
		listResult(result);
	}
	// end retrieveByDisjunction

	private static void retrieveByComparison(ObjectContainer container) {
		Query query = container.query();
		query.constrain(Pilot.class);
		query.descend("points").constrain(new Integer(99)).greater();
		ObjectSet result = query.execute();
		listResult(result);
	}
	// end retrieveByComparison

	private static void retrieveByDefaultFieldValue(
			ObjectContainer container) {
		Pilot somebody = new Pilot("Somebody else", 0);
		container.store(somebody);
		Query query = container.query();
		query.constrain(Pilot.class);
		query.descend("points").constrain(new Integer(0));
		ObjectSet result = query.execute();
		listResult(result);
		container.delete(somebody);
	}
	// end retrieveByDefaultFieldValue

	private static void retrieveSorted(ObjectContainer container) {
		Query query = container.query();
		query.constrain(Pilot.class);
		query.descend("name").orderAscending();
		ObjectSet result = query.execute();
		listResult(result);
		query.descend("name").orderDescending();
		result = query.execute();
		listResult(result);
	}
	// end retrieveSorted

	private static void clearDatabase(ObjectContainer container) {
		ObjectSet result = container.queryByExample(Pilot.class);
		while (result.hasNext()) {
			container.delete(result.next());
		}
	}
	// end clearDatabase

	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult
}
