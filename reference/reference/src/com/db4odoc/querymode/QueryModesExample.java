/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.querymode;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.tools.*;

public class QueryModesExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		Db4o.configure().objectClass(Pilot.class).objectField(
				"points").indexed(true);
		// testImmediateQueries();
		// testLazyQueries();
		// testSnapshotQueries();
		testLazyConcurrent();
		// testSnapshotConcurrent();
		// testImmediateChanged();
	}
	// end main

	private static void fillUpDB(int pilotCount) {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			for (int i = 0; i < pilotCount; i++) {
				addPilot(container, i);
			}
		} finally {
			container.close();
		}
	}
	// end fillUpDB

	private static void addPilot(ObjectContainer container, int points) {
		Pilot pilot = new Pilot("Tester", points);
		container.store(pilot);
	}
	// end addPilot

	private static void testImmediateQueries() {
		System.out
				.println("Testing query performance on 10000 pilot objects in Immediate mode");
		fillUpDB(10000);
		Configuration configuration = Db4o.newConfiguration();
		configuration.queries().evaluationMode(QueryEvaluationMode.IMMEDIATE);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			QueryStats stats = new QueryStats();
			stats.connect(container);
			Query query = container.query();
			query.constrain(Pilot.class);
			query.descend("points").constrain(99).greater();
			query.execute();
			long executionTime = stats.executionTime();
			System.out.println("Query execution time: "
					+ executionTime);
		} finally {
			container.close();
		}
	}
	// end testImmediateQueries

	private static void testLazyQueries() {
		System.out
				.println("Testing query performance on 10000 pilot objects in Lazy mode");
		fillUpDB(10000);
		Configuration configuration = Db4o.newConfiguration();
		configuration.queries().evaluationMode(QueryEvaluationMode.LAZY);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			QueryStats stats = new QueryStats();
			stats.connect(container);
			Query query = container.query();
			query.constrain(Pilot.class);
			query.descend("points").constrain(99).greater();
			query.execute();
			long executionTime = stats.executionTime();
			System.out.println("Query execution time: "
					+ executionTime);
		} finally {
			container.close();
		}
	}
	// end testLazyQueries

	private static void testLazyConcurrent() {
		System.out
				.println("Testing lazy mode with concurrent modifications");
		fillUpDB(10);
		Configuration configuration = Db4o.newConfiguration();
		configuration.queries().evaluationMode(QueryEvaluationMode.LAZY);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Query query1 = container.query();
			query1.constrain(Pilot.class);
			query1.descend("points").constrain(5).smaller();
			ObjectSet result1 = query1.execute();

			Query query2 = container.query();
			query2.constrain(Pilot.class);
			query2.descend("points").constrain(1);
			ObjectSet result2 = query2.execute();
			Pilot pilotToDelete = (Pilot) result2.get(0);
			System.out.println("Pilot to be deleted: "
					+ pilotToDelete);
			container.delete(pilotToDelete);
			Pilot pilot = new Pilot("Tester", 2);
			System.out.println("Pilot to be added: " + pilot);
			container.store(pilot);

			System.out
					.println("Query result after changing from the same transaction");
			listResult(result1);
		} finally {
			container.close();
		}
	}
	// end testLazyConcurrent

	private static void listResult(ObjectSet result) {
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult

	private static void testSnapshotQueries() {
		System.out
				.println("Testing query performance on 10000 pilot objects in Snapshot mode");
		fillUpDB(10000);
		Configuration configuration = Db4o.newConfiguration();
		configuration.queries().evaluationMode(QueryEvaluationMode.SNAPSHOT);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			QueryStats stats = new QueryStats();
			stats.connect(container);
			Query query = container.query();
			query.constrain(Pilot.class);
			query.descend("points").constrain(99).greater();
			query.execute();
			long executionTime = stats.executionTime();
			System.out.println("Query execution time: "
					+ executionTime);
		} finally {
			container.close();
		}
	}
	// end testSnapshotQueries

	private static void testSnapshotConcurrent() {
		System.out
				.println("Testing snapshot mode with concurrent modifications");
		fillUpDB(10);
		Configuration configuration = Db4o.newConfiguration();
		configuration.queries().evaluationMode(QueryEvaluationMode.SNAPSHOT);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Query query1 = container.query();
			query1.constrain(Pilot.class);
			query1.descend("points").constrain(5).smaller();
			ObjectSet result1 = query1.execute();

			Query query2 = container.query();
			query2.constrain(Pilot.class);
			query2.descend("points").constrain(1);
			ObjectSet result2 = query2.execute();
			Pilot pilotToDelete = (Pilot) result2.get(0);
			System.out.println("Pilot to be deleted: "
					+ pilotToDelete);
			container.delete(pilotToDelete);
			Pilot pilot = new Pilot("Tester", 2);
			System.out.println("Pilot to be added: " + pilot);
			container.store(pilot);

			System.out
					.println("Query result after changing from the same transaction");
			listResult(result1);
		} finally {
			container.close();
		}
	}
	// end testSnapshotConcurrent

	private static void testImmediateChanged() {
		System.out
				.println("Testing immediate mode with field changes");
		fillUpDB(10);
		Configuration configuration = Db4o.newConfiguration();
		configuration.queries().evaluationMode(QueryEvaluationMode.IMMEDIATE);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Query query1 = container.query();
			query1.constrain(Pilot.class);
			query1.descend("points").constrain(5).smaller();
			ObjectSet result1 = query1.execute();

			// change field
			Query query2 = container.query();
			query2.constrain(Pilot.class);
			query2.descend("points").constrain(2);
			ObjectSet result2 = query2.execute();
			Pilot pilot2 = (Pilot) result2.get(0);
			pilot2.addPoints(22);
			container.store(pilot2);
			listResult(result1);
		} finally {
			container.close();
		}
	}
	// end testImmediateChanged
}
