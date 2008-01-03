/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.utility;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4odoc.activating.SensorPanel;

public class UtilityExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		testDescend();
		checkActive();
		checkStored();
	}

	// end main

	private static void storeSensorPanel() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			// create a linked list with length 10
			SensorPanel list = new SensorPanel().createList(10);
			// store all elements with one statement, since all
			// elements are new
			container.store(list);
		} finally {
			container.close();
		}
	}

	// end storeSensorPanel

	private static void testDescend() {
		storeSensorPanel();
		Configuration configuration = Db4o.newConfiguration();
		configuration.activationDepth(1);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			System.out
					.println("Object container activation depth = 1");
			ObjectSet result = container.queryByExample(new SensorPanel(1));
			SensorPanel spParent = (SensorPanel) result.get(0);
			SensorPanel spDescend = (SensorPanel) container.ext()
					.descend(
							(Object) spParent,
							new String[] { "next", "next", "next",
									"next", "next" });
			container.ext().activate(spDescend, 5);
			System.out.println(spDescend);
		} finally {
			container.close();
		}
	}

	// end testDescend

	private static void checkActive() {
		storeSensorPanel();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			container.ext().configure().activationDepth(2);
			System.out
					.println("Object container activation depth = 2");
			ObjectSet result = container.queryByExample(new SensorPanel(1));
			SensorPanel sensor = (SensorPanel) result.get(0);
			SensorPanel next = sensor.next;
			while (next != null) {
				System.out.println("Object " + next + " is active: "
						+ container.ext().isActive(next));
				next = next.next;
			}
		} finally {
			container.close();
		}
	}

	// end checkActive

	private static void checkStored() {
		// create a linked list with length 10
		SensorPanel list = new SensorPanel().createList(10);
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			// store all elements with one statement, since all
			// elements are new
			container.store(list);
			Object sensor = (Object) list.sensor;
			SensorPanel sp5 = list.next.next.next.next;
			System.out.println("Root element " + list + " isStored: "
					+ container.ext().isStored(list));
			System.out.println("Simple type  " + sensor
					+ " isStored: "
					+ container.ext().isStored(sensor));
			System.out.println("Descend element  " + sp5
					+ " isStored: " + container.ext().isStored(sp5));
			container.delete(list);
			System.out.println("Root element " + list + " isStored: "
					+ container.ext().isStored(list));
		} finally {
			container.close();
		}
	}
	// end checkStored
}
