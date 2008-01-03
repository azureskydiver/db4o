/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.persist;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class PeekPersistedExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		measureCarTemperature();
	}

	// end main

	private static void setObjects() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Car car = new Car("BMW");
			container.store(car);
		} finally {
			container.close();
		}
	}
	// end setObjects

	private static void measureCarTemperature() {
		setObjects();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.query(Car.class);
			if (result.size() > 0) {
				Car car = (Car) result.get(0);
				Car car1 = (Car) container.ext().peekPersisted(car,
						5, true);
				Change1 ch1 = new Change1();
				ch1.init(car1);
				Car car2 = (Car) container.ext().peekPersisted(car,
						5, true);
				Change2 ch2 = new Change2();
				ch2.init(car2);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
				}
				// We can work on the database object at the same time
				car.setModel("BMW M3Coupe");
				container.store(car);
				ch1.stop();
				ch2.stop();
				System.out.println("car1 saved to the database: "
						+ container.ext().isStored(car1));
				System.out.println("car2 saved to the database: "
						+ container.ext().isStored(car1));
				int temperature = (int) ((car1.getTemperature() + car2
						.getTemperature()) / 2);
				car.setTemperature(temperature);
				container.store(car);
			}
		} finally {
			container.close();
		}
		checkCar();
	}
	// end measureCarTemperature

	private static void checkCar() {
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.query(Car.class);
			listResult(result);
		} finally {
			container.close();
		}
	}
	// end checkCar

	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult
}
