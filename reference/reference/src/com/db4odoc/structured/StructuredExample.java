/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.structured;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

public class StructuredExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			storeFirstCar(container);
			storeSecondCar(container);
			//retrieveAllCarsQBE(container);
			//retrieveAllPilotsQBE(container);
			//retrieveCarByPilotQBE(container);
			retrieveCarByPilotNameQuery(container);
			//retrieveCarByPilotProtoQuery(container);
			retrievePilotByCarModelQuery(container);
//			updateCar(container);
//			updatePilotSingleSession(container);
//			updatePilotSeparateSessionsPart1(container);
//			container.close();
//			container = Db4o.openFile(DB4O_FILE_NAME);
//			updatePilotSeparateSessionsPart2(container);
//			container.close();
//			Configuration configuration = updatePilotSeparateSessionsImprovedPart1();
//			container = Db4o.openFile(configuration, DB4O_FILE_NAME);
//			updatePilotSeparateSessionsImprovedPart2(container);
//			container.close();
//			container = Db4o.openFile(configuration, DB4O_FILE_NAME);
//			updatePilotSeparateSessionsImprovedPart3(container);
//			deleteFlat(container);
//			container.close();
//			configuration = deleteDeepPart1();
//			container = Db4o.openFile(configuration, DB4O_FILE_NAME);
//			deleteDeepPart2(container);
//			deleteDeepRevisited(container);
		} finally {
			container.close();
		}
	}

	// end main

	private static void storeFirstCar(ObjectContainer container) {
		Car car1 = new Car("Ferrari");
		Pilot pilot1 = new Pilot("Michael Schumacher", 100);
		car1.setPilot(pilot1);
		container.store(car1);
	}

	// end storeFirstCar

	private static void storeSecondCar(ObjectContainer container) {
		Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
		container.store(pilot2);
		Car car2 = new Car("BMW");
		car2.setPilot(pilot2);
		container.store(car2);
	}

	// end storeSecondCar

	private static void retrieveAllCarsQBE(ObjectContainer container) {
		Car proto = new Car(null);
		ObjectSet result = container.queryByExample(proto);
		listResult(result);
	}

	// end retrieveAllCarsQBE

	private static void retrieveAllPilotsQBE(ObjectContainer container) {
		Pilot proto = new Pilot(null, 0);
		ObjectSet result = container.queryByExample(proto);
		listResult(result);
	}

	// end retrieveAllPilotsQBE

	private static void retrieveAllPilots(ObjectContainer container) {
		ObjectSet result = container.queryByExample(Pilot.class);
		listResult(result);
	}

	// end retrieveAllPilots

	private static void retrieveCarByPilotQBE(ObjectContainer container) {
		Pilot pilotproto = new Pilot("Rubens Barrichello", 0);
		Car carproto = new Car(null);
		carproto.setPilot(pilotproto);
		ObjectSet result = container.queryByExample(carproto);
		listResult(result);
	}

	// end retrieveCarByPilotQBE

	private static void retrieveCarByPilotNameQuery(
			ObjectContainer container) {
		Query query = container.query();
		query.constrain(Car.class);
		query.descend("pilot").descend("name").constrain(
				"Rubens Barrichello");
		ObjectSet result = query.execute();
		listResult(result);
	}

	// end retrieveCarByPilotNameQuery

	private static void retrieveCarByPilotProtoQuery(
			ObjectContainer container) {
		Query query = container.query();
		query.constrain(Car.class);
		Pilot proto = new Pilot("Rubens Barrichello", 0);
		query.descend("pilot").constrain(proto);
		ObjectSet result = query.execute();
		listResult(result);
	}

	// end retrieveCarByPilotProtoQuery

	private static void retrievePilotByCarModelQuery(
			ObjectContainer container) {
		Query carquery = container.query();
		carquery.constrain(Car.class);
		carquery.descend("model").constrain("Ferrari");
		Query pilotquery = carquery.descend("pilot");
		ObjectSet result = pilotquery.execute();
		listResult(result);
	}

	// end retrievePilotByCarModelQuery

	private static void retrieveAllPilotsNative(
			ObjectContainer container) {
		ObjectSet results = container.query(new Predicate<Pilot>() {
			public boolean match(Pilot pilot) {
				return true;
			}
		});
		listResult(results);
	}

	// end retrieveAllPilotsNative

	private static void retrieveAllCars(ObjectContainer container) {
		ObjectSet results = container.queryByExample(Car.class);
		listResult(results);
	}

	// end retrieveAllCars

	private static void retrieveCarsByPilotNameNative(
			ObjectContainer container) {
		final String pilotName = "Rubens Barrichello";
		ObjectSet results = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getPilot().getName().equals(pilotName);
			}
		});
		listResult(results);
	}

	// end retrieveCarsByPilotNameNative

	private static void updateCar(ObjectContainer container) {
		ObjectSet result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		Car found = (Car) result.next();
		found.setPilot(new Pilot("Somebody else", 0));
		container.store(found);
		result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		listResult(result);
	}

	// end updateCar

	private static void updatePilotSingleSession(
			ObjectContainer container) {
		ObjectSet result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		Car found = (Car) result.next();
		found.getPilot().addPoints(1);
		container.store(found);
		result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		listResult(result);
	}

	// end updatePilotSingleSession

	private static void updatePilotSeparateSessionsPart1(
			ObjectContainer container) {
		ObjectSet result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		Car found = (Car) result.next();
		found.getPilot().addPoints(1);
		container.store(found);
	}

	// end updatePilotSeparateSessionsPart1

	private static void updatePilotSeparateSessionsPart2(
			ObjectContainer container) {
		ObjectSet result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		listResult(result);
	}

	// end updatePilotSeparateSessionsPart2

	private static Configuration updatePilotSeparateSessionsImprovedPart1() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass("com.db4o.f1.chapter2.Car")
				.cascadeOnUpdate(true);
		return configuration;
	}

	// end updatePilotSeparateSessionsImprovedPart1

	private static void updatePilotSeparateSessionsImprovedPart2(
			ObjectContainer container) {
		ObjectSet result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		Car found = (Car) result.next();
		found.getPilot().addPoints(1);
		container.store(found);
	}

	// end updatePilotSeparateSessionsImprovedPart2

	private static void updatePilotSeparateSessionsImprovedPart3(
			ObjectContainer container) {
		ObjectSet result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		listResult(result);
	}

	// end updatePilotSeparateSessionsImprovedPart3

	private static void deleteFlat(ObjectContainer container) {
		ObjectSet result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		Car found = (Car) result.next();
		container.delete(found);
		result = container.queryByExample(new Car(null));
		listResult(result);
	}

	// end deleteFlat

	private static Configuration deleteDeepPart1() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass("com.db4o.f1.chapter2.Car")
				.cascadeOnDelete(true);
		return configuration;
	}

	// end deleteDeepPart1

	private static void deleteDeepPart2(ObjectContainer container) {
		ObjectSet result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("BMW");
			}
		});
		Car found = (Car) result.next();
		container.delete(found);
		result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return true;
			}
		});
		listResult(result);
	}

	// end deleteDeepPart2

	private static void deleteDeepRevisited(ObjectContainer container) {
		ObjectSet result = container.query(new Predicate<Pilot>() {
			public boolean match(Pilot pilot) {
				return pilot.getName().equals("Michael Schumacher");
			}
		});
		Pilot pilot = (Pilot) result.next();
		Car car1 = new Car("Ferrari");
		Car car2 = new Car("BMW");
		car1.setPilot(pilot);
		car2.setPilot(pilot);
		container.store(car1);
		container.store(car2);
		container.delete(car2);
		result = container.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return true;
			}
		});
		listResult(result);
	}

	// end deleteDeepRevisited

	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult
}
