/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.queries;

import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

public class NQExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			storePilots(container);
			retrieveComplexSODA(container);
			retrieveComplexNQ(container);
			retrieveArbitraryCodeNQ(container);
			clearDatabase(container);
		} finally {
			container.close();
		}
	}
	// end main

	private static void primitiveQuery(ObjectContainer container) {
		List<Pilot> pilots = container.query(new Predicate<Pilot>() {
			public boolean match(Pilot pilot) {
				return pilot.getPoints() == 100;
			}
		});
	}
	// end primitiveQuery

	private static void advancedQuery(ObjectContainer container) {
		List<Pilot> result = container.query(new Predicate<Pilot>() {
			public boolean match(Pilot pilot) {
				return pilot.getPoints() > 99
						&& pilot.getPoints() < 199
						|| pilot.getName().equals(
								"Rubens Barrichello");
			}
		});
	}
	// end advancedQuery

	private static void storePilots(ObjectContainer container) {
		container.store(new Pilot("Michael Schumacher", 100));
		container.store(new Pilot("Rubens Barrichello", 99));
	}
	// end storePilots

	private static void retrieveComplexSODA(ObjectContainer container) {
		Query query = container.query();
		query.constrain(Pilot.class);
		Query pointQuery = query.descend("points");
		query.descend("name").constrain("Rubens Barrichello").or(
				pointQuery.constrain(new Integer(99)).greater().and(
						pointQuery.constrain(new Integer(199))
								.smaller()));
		ObjectSet result = query.execute();
		listResult(result);
	}
	// end retrieveComplexSODA

	private static void retrieveComplexNQ(ObjectContainer container) {
		ObjectSet result = container.query(new Predicate<Pilot>() {
			public boolean match(Pilot pilot) {
				return pilot.getPoints() > 99
						&& pilot.getPoints() < 199
						|| pilot.getName().equals(
								"Rubens Barrichello");
			}
		});
		listResult(result);
	}
	// end retrieveComplexNQ

	private static void retrieveArbitraryCodeNQ(
			ObjectContainer container) {
		final int[] points = { 1, 100 };
		ObjectSet result = container.query(new Predicate<Pilot>() {
			public boolean match(Pilot pilot) {
				for (int i = 0; i < points.length; i++) {
					if (pilot.getPoints() == points[i]) {
						return true;
					}
				}
				return pilot.getName().startsWith("Rubens");
			}
		});
		listResult(result);
	}
	// end retrieveArbitraryCodeNQ

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
