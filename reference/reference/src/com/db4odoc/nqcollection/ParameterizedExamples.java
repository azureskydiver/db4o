/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.nqcollection;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
/**
 * @sharpen.ignore
 */
public class ParameterizedExamples {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private final static int OBJECT_COUNT = 10;

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		storePilots();
		getTestPilots();
		getProfessionalPilots();
	}

	// end main

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

	private static void storePilots() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database();
		if (container != null) {
			try {
				Pilot pilot;
				for (int i = 0; i < OBJECT_COUNT; i++) {
					pilot = new Pilot("Test Pilot #" + i, i);
					container.store(pilot);
				}
				for (int i = 0; i < OBJECT_COUNT; i++) {
					pilot = new Pilot("Professional Pilot #" + (i + 10), i + 10);
					container.store(pilot);
				}
				container.commit();
			} catch (Db4oException ex) {
				System.out.println("Db4o Exception: " + ex.getMessage());
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end storePilots

	private static class PilotNamePredicate extends com.db4o.query.Predicate<Pilot> {
		private String startsWith;

		public PilotNamePredicate(String startsWith) {
			this.startsWith = startsWith;
		}

		public boolean match(Pilot pilot) {
			return pilot.getName().startsWith(startsWith);
		}
	}

	// end PilotNamePredicate

	private static void getTestPilots() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = container.query(new PilotNamePredicate(
						"Test"));
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end getTestPilots

	private static List<Pilot> byNameBeginning(final String startsWith) {
		return database().query(new com.db4o.query.Predicate<Pilot>() {
			public boolean match(Pilot pilot) {
				return pilot.getName().startsWith(startsWith);
			}
		});
	}

	// end byNameBeginning

	private static void getProfessionalPilots() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Pilot> result = byNameBeginning("Professional");
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end getProfessionalPilots

	private static void listResult(List result) {
		System.out.println(result.size());
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
	}

	// end listResult

}
