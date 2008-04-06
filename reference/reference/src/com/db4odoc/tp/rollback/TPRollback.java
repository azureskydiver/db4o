/* Copyright (C) 2008 db4objects Inc. http://www.db4o.com */
package com.db4odoc.tp.rollback;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ta.RollbackStrategy;
import com.db4o.ta.TransparentPersistenceSupport;

public class TPRollback {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private static ObjectContainer _container = null;

	public static void main(String[] args) throws CloneNotSupportedException {
		storeCar();
		modifyAndRollback();
		modifyRollbackAndCheck();
		modifyWithRollbackStrategy();
	}

	// end main

	private static void storeCar() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database(configureTP());
		if (container != null) {
			try {
				// create a car
				Car car = new Car("BMW", new Pilot("Rubens Barrichello", 1));
				container.store(car);
				container.commit();
			} finally {
				closeDatabase();
			}
		}
	}

	// end storeCar

	private static void modifyAndRollback() {
		ObjectContainer container = database(configureTP());
		if (container != null) {
			try {
				// create a car
				Car car = (Car) container.queryByExample(new Car(null, null))
						.get(0);
				System.out.println("Initial car: " + car + "("
						+ container.ext().getID(car) + ")");
				car.setModel("Ferrari");
				car.setPilot(new Pilot("Michael Schumacher", 123));
				container.rollback();
				System.out.println("Car after rollback: " + car + "("
						+ container.ext().getID(car) + ")");
			} finally {
				closeDatabase();
			}
		}
	}

	// end modifyAndRollback

	private static void modifyRollbackAndCheck() {
		ObjectContainer container = database(configureTP());
		if (container != null) {
			try {
				// create a car
				Car car = (Car) container.queryByExample(new Car(null, null))
						.get(0);
				Pilot pilot = car.getPilot();
				System.out.println("Initial car: " + car + "("
						+ container.ext().getID(car) + ")");
				System.out.println("Initial pilot: " + pilot + "("
						+ container.ext().getID(pilot) + ")");
				car.setModel("Ferrari");
				car.changePilot("Michael Schumacher", 123);
				container.rollback();
				container.deactivate(car, Integer.MAX_VALUE);
				System.out.println("Car after rollback: " + car + "("
						+ container.ext().getID(car) + ")");
				System.out.println("Pilot after rollback: " + pilot + "("
						+ container.ext().getID(pilot) + ")");
			} finally {
				closeDatabase();
			}
		}
	}

	// end modifyRollbackAndCheck

	private static void modifyWithRollbackStrategy() {
		ObjectContainer container = database(configureTPForRollback());
		if (container != null) {
			try {
				// create a car
				Car car = (Car) container.queryByExample(new Car(null, null))
						.get(0);
				Pilot pilot = car.getPilot();
				System.out.println("Initial car: " + car + "("
						+ container.ext().getID(car) + ")");
				System.out.println("Initial pilot: " + pilot + "("
						+ container.ext().getID(pilot) + ")");
				car.setModel("Ferrari");
				car.changePilot("Michael Schumacher", 123);
				container.rollback();
				System.out.println("Car after rollback: " + car + "("
						+ container.ext().getID(car) + ")");
				System.out.println("Pilot after rollback: " + pilot + "("
						+ container.ext().getID(pilot) + ")");
			} finally {
				closeDatabase();
			}
		}
	}
	// end modifyWithRollbackStrategy

	private static Configuration configureTP() {
		Configuration configuration = Db4o.newConfiguration();
		// add TP support
		configuration.add(new TransparentPersistenceSupport());
		return configuration;
	}

	// end configureTP

	private static Configuration configureTPForRollback() {
		Configuration configuration = Db4o.newConfiguration();
		// add TP support and rollback strategy
		configuration.add(new TransparentPersistenceSupport(
				new RollbackDeactivateStrategy()));
		return configuration;
	}

	// end configureTPForRollback

	
	private static class RollbackDeactivateStrategy implements RollbackStrategy {
		public void rollback(ObjectContainer container, Object obj) {
			container.ext().deactivate(obj);
		}
	}

	// end RollbackDeactivateStrategy

	private static ObjectContainer database(Configuration configuration) {
		if (_container == null) {
			try {
				_container = Db4o.openFile(configuration, DB4O_FILE_NAME);
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

}
