/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.nqcollection;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;

/**
 * @sharpen.ignore
 */

public class ComplexParameterizedExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private final static int OBJECT_COUNT = 5;

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		storePersons();
		getTrainees();
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

	private static void storePersons() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database();
		if (container != null) {
			try {
				Trainee trainee;
				// store OBJECT_COUNT pilots and trainees
				for (int i = 0; i < OBJECT_COUNT; i++) {
					trainee = new Trainee("Trainee #" + i, new Pilot(
							"Professional Pilot #" + i, i));
					container.store(trainee);
				}
				// store a new trainee with a "Training" pilot
				trainee = new Trainee("Trainee #1", new Pilot(
						"Training Pilot #1", 20));
				container.store(trainee);
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

	// end storePersons

	private static class PersonNamePredicate<T extends Person> extends
	com.db4o.query.Predicate<T> {
		private String startsWith;

		public PersonNamePredicate(String startsWith) {
			this.startsWith = startsWith;
		}

		public PersonNamePredicate(Class<T> clazz, String startsWith) {
			super(clazz);
			this.startsWith = startsWith;
		}

		public boolean match(T candidate) {
			return candidate.getName().startsWith(startsWith);
		}
	}

	// end PersonNamePredicate

	private static void getTrainees() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				// query for Trainee objects starting with "Train".
				// Wrongly created predicate mixes Trainee and Pilot
				// objects and creates a resultset based on only "Tr"
				// criteria (class of an object is not considered)

				testQuery(container, createPredicateWrong(Trainee.class,
						"Train"));
				// Correctly created result set returns only objects
				// of the requested class
				testQuery(container, createPredicateCorrect(Trainee.class,
						"Train"));
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end getTrainees

	private static void testQuery(ObjectContainer container,
			com.db4o.query.Predicate<Trainee> predicate) {
		List<Trainee> result = container.query(predicate);
		System.out.println(result.size());
		try {
			for (Trainee trainee : result) {
				System.out.println(trainee);
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	// end testQuery

	private static <T extends Person> com.db4o.query.Predicate<T> createPredicateWrong(
			Class<T> clazz, String startsWith) {
		return new PersonNamePredicate<T>(startsWith);
	}

	// end createPredicateWrong

	private static <T extends Person> com.db4o.query.Predicate<T> createPredicateCorrect(
			Class<T> clazz, String startsWith) {
		return new PersonNamePredicate<T>(clazz, startsWith);
	}
	// end createPredicateCorrect

}
