/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.metainfo;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;

public class MetaInfoExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		setObjects();
		getMetaObjects();
		getMetaObjectsInfo();
	}

	// end main

	private static void setObjects() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
			container.set(car);
			car = new Car("Ferrari", new Pilot("Michael Schumacher"));
			container.set(car);
		} finally {
			container.close();
		}
	}

	// end setObjects

	private static void getMetaObjects() {
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			System.out
					.println("Retrieve meta information for class: ");
			StoredClass sc = container.ext().storedClass(
					Car.class.getName());
			System.out.println("Stored class:  " + sc.toString());

			System.out
					.println("Retrieve meta information for all classes in database: ");
			StoredClass sclasses[] = container.ext().storedClasses();
			for (int i = 0; i < sclasses.length; i++) {
				System.out.println(sclasses[i].getName());
			}
		} finally {
			container.close();
		}
	}

	// end getMetaObjects

	private static void getMetaObjectsInfo() {
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			System.out
					.println("Retrieve meta information for field: ");
			StoredClass sc = container.ext().storedClass(
					Car.class.getName());
			StoredField sf = sc.storedField("pilot", Pilot.class);
			System.out
					.println("Field info:  " + sf.getName() + "/"
							+ sf.getStoredType() + "/isArray="
							+ sf.isArray());

			System.out.println("Retrieve all fields: ");
			StoredField sfields[] = sc.getStoredFields();
			for (int i = 0; i < sfields.length; i++) {
				System.out.println("Stored field:  "
						+ sfields[i].getName() + "/"
						+ sfields[i].getStoredType());
			}
		} finally {
			container.close();
		}
	}
	// end getMetaObjectsInfo
}
