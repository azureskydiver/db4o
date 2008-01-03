/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.equality;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.query.Predicate;


public class EqualityExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		storePilot();
		testEquality();
		retrieveEqual();
	}

	private static ObjectContainer database() {
		if (_container == null) {
			try {
				_container = Db4o.openFile(DB4O_FILE_NAME);
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

	private static void storePilot() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				Pilot pilot = new Pilot("Kimi Raikkonnen", 100);
				container.store(pilot);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end storePilot

	private static void testEquality() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				ObjectSet<Pilot> result = container.query(new Predicate<Pilot>(){
					public boolean match(Pilot pilot){
						return pilot.getName().equals("Kimi Raikkonnen") &&
						   pilot.getPoints() == 100;
					}
				});
		         Pilot obj = (Pilot)result.next();
		         Pilot pilot = new Pilot("Kimi Raikkonnen", 100);
		         String equality = obj.equals(pilot) ? "equal" : "not equal";
		         System.out.println("Pilots are " + equality);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end testEquality

	private static void retrieveEqual() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				ObjectSet result = container.queryByExample(new Pilot("Kimi Raikkonnen", 100));
				if (result.size() > 0){
					System.out.println("Found equal object: " + result.next().toString());
				} else {
					System.out.println("No equal object exist in the database");
				}
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end retrieveEqual

}
