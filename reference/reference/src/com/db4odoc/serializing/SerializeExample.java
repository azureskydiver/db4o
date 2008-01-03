/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.serializing;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SerializeExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	private final static String XMLXML_FILE_NAME = "reference.xml";

	public static void main(String[] args) {
		setObjects();
		exportToXml();
		importFromXml();
	}

	// end main

	private static void setObjects() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
			container.store(car);
			car = new Car("Ferrari", new Pilot("Michael Schumacher"));
			container.store(car);
		} finally {
			container.close();
		}
	}

	// end setObjects

	private static void exportToXml() {
		XStream xstream = new XStream(new DomDriver());
		try {
			FileWriter xmlFile = new FileWriter(XMLXML_FILE_NAME);
			ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
			try {
				ObjectSet result = container.query(Car.class);
				Car[] cars = new Car[result.size()];
				for (int i = 0; i < result.size(); i++) {
					Car car = (Car) result.next();
					cars[i] = car;
				}
				String xml = xstream.toXML(cars);
				xmlFile.write("<?xml version=\"1.0\"?>\n" + xml);
				xmlFile.close();
			} finally {
				container.close();
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	// end exportToXml

	private static void importFromXml() {
		new File(DB4O_FILE_NAME).delete();
		XStream xstream = new XStream(new DomDriver());
		try {
			FileReader xmlReader = new FileReader(XMLXML_FILE_NAME);
			Car[] cars = (Car[]) xstream.fromXML(xmlReader);
			ObjectContainer container;
			for (int i = 0; i < cars.length; i++) {
				container = Db4o.openFile(DB4O_FILE_NAME);
				try {
					Car car = (Car) cars[i];
					container.store(car);
				} finally {
					container.close();
				}
			}
			container = Db4o.openFile(DB4O_FILE_NAME);
			try {
				ObjectSet result = container.query(Pilot.class);
				listResult(result);
				result = container.query(Car.class);
				listResult(result);
			} finally {
				container.close();
			}
			xmlReader.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	// end importFromXml

	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult

}
