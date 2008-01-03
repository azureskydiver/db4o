/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.qbe;

import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4odoc.nqcollection.Pilot;

public class QBEExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private final static int OBJECT_COUNT = 10;

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		test();
		test1();
		test2();
		test3();
		test4();
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

	private static void test() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				Pilot pilot = new Pilot("Kimi Raikkonen", 100);
				container.store(pilot);
				ObjectSet result = container.queryByExample(new Pilot("Kimi Raikkonen", 100));
				System.out.println("Test QBE");
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end test

	private static void test1() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				// Pilot1 contains initialisation in the constructor
				Pilot1 pilot = new Pilot1("Kimi Raikkonen");
				container.store(pilot);
				// QBE does not return any results
				ObjectSet result = container.queryByExample(new Pilot1("Kimi Raikonnen"));
				System.out.println("Test QBE on class with member initialization in constructor");
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end test1

	private static void test2() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				// Pilot1Derived derives the constructor with initialisation
				Pilot1Derived pilot = new Pilot1Derived("Kimi Raikkonen");
				container.store(pilot);
				// QBE does not return any results
				ObjectSet result = container.queryByExample(new Pilot1Derived("Kimi Raikonnen"));
				System.out.println("Test QBE on class with member initialization in ancestor constructor");
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end test2


	private static void test3() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				// Pilot2 uses static initialization of points member
				Pilot2 pilot = new Pilot2("Kimi Raikkonen");
				container.store(pilot);
				// QBE does not return any results
				ObjectSet result = container.queryByExample(new Pilot2("Kimi Raikonnen"));
				System.out.println("Test QBE on class with static member initialization");
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end test3

	private static void test4() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				// Pilot2Derived is derived from class with static initialization of points member
				Pilot2Derived pilot = new Pilot2Derived("Kimi Raikkonen");
				container.store(pilot);
				// QBE does not return any results
				ObjectSet result = container.queryByExample(new Pilot2Derived("Kimi Raikonnen"));
				System.out.println("Test QBE on class derived from a class with static member initialization");
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end test4
	
	

	private static void listResult(List result) {
		System.out.println(result.size());
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
	}

	// end listResult
}
