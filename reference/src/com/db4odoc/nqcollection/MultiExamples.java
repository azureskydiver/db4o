/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.nqcollection;

import java.io.File;
import java.util.List;

import com.db4o.DatabaseFileLockedException;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ext.Db4oException;
import com.db4o.query.Predicate;

public class MultiExamples {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private final static int OBJECT_COUNT = 10;

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		storePilotsAndTrainees();
		selectPilotsAndTrainees();
		storeCars();
		selectPilotsInRange();

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

	private static void storePilotsAndTrainees() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database();
		if (container != null) {
			try {
				Pilot pilot;
				Trainee trainee;
				for (int i = 0; i < OBJECT_COUNT; i++) {
					pilot = new Pilot("Professional Pilot #" + i, i);
					trainee = new Trainee("Trainee #" + i, pilot);
					container.set(trainee);
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

	// end storePilotsAndTrainees

	private static void storeCars() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database();
		if (container != null) {
			try {
				Car car;
				for (int i = 0; i < OBJECT_COUNT; i++) {
					car = new Car("BMW", new Pilot("Test Pilot #" + i, i));
					container.set(car);
				}
				for (int i = 0; i < OBJECT_COUNT; i++) {
					car = new Car("Ferrari", new Pilot("Professional Pilot #"
							+ (i + 10), (i + 10)));
					container.set(car);
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

	// end storeCars

	private static void selectPilotsAndTrainees() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Person> result = container.query(new Predicate<Person>() {
					public boolean match(Person person) {
						// all persons
						return true;
					}
				});
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end selectPilotsAndTrainees

	private static void selectPilotsInRange() {
		ObjectContainer container = database();
		if (container != null) {
			try {
				List<Car> result = container.query(new Predicate<Car>() {
					private List<Pilot> pilots = null;

					private List getPilotsList() {
						if (pilots == null) {
							pilots = database().query(new Predicate<Pilot>() {
								public boolean match(Pilot pilot) {
									return pilot.getName().startsWith("Test");
								}
							});
						}
						return pilots;
					}

					public boolean match(Car car) {
						// all Cars that have pilot field in the
						// Pilots array
						return getPilotsList().contains(car.getPilot());
					}
				});
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end selectPilotsInRange

	private static void listResult(List result) {
		System.out.println(result.size());
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
	}
	// end listResult

}
