/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.uuids;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

public class UUIDExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		testChangeIdentity();
		setObjects();
		testGenerateUUID();
	}

	// end main

	private static String printSignature(byte[] signature) {
		String str = "";
		for (int i = 0; i < signature.length; i++) {
			str = str + signature[i];
		}
		return str;
	}

	// end printSignature

	private static void testChangeIdentity() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		Db4oDatabase db;
		byte[] oldSignature;
		byte[] newSignature;
		try {
			db = container.ext().identity();
			oldSignature = db.getSignature();
			System.out.println("oldSignature: "
					+ printSignature(oldSignature));
			((LocalObjectContainer) container).generateNewIdentity();
		} finally {
			container.close();
		}
		container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			db = container.ext().identity();
			newSignature = db.getSignature();
			System.out.println("newSignature: "
					+ printSignature(newSignature));
		} finally {
			container.close();
		}

		boolean same = true;

		for (int i = 0; i < oldSignature.length; i++) {
			if (oldSignature[i] != newSignature[i]) {
				same = false;
			}
		}

		if (same) {
			System.out.println("Database signatures are identical");
		} else {
			System.out.println("Database signatures are different");
		}
	}

	// end testChangeIdentity

	private static void setObjects() {
		new File(DB4O_FILE_NAME).delete();
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(Pilot.class).generateUUIDs(true);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
			container.store(car);
		} finally {
			container.close();
		}
	}

	// end setObjects

	private static void testGenerateUUID() {
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Query query = container.query();
			query.constrain(Car.class);
			ObjectSet result = query.execute();
			Car car = (Car) result.get(0);
			ObjectInfo carInfo = container.ext().getObjectInfo(car);
			Db4oUUID carUUID = carInfo.getUUID();
			System.out
					.println("UUID for Car class are not generated:");
			System.out.println("Car UUID: " + carUUID);

			Pilot pilot = car.getPilot();
			ObjectInfo pilotInfo = container.ext().getObjectInfo(
					pilot);
			Db4oUUID pilotUUID = pilotInfo.getUUID();
			System.out.println("UUID for Pilot:");
			System.out.println("Pilot UUID: " + pilotUUID);
			System.out.println("long part: "
					+ pilotUUID.getLongPart() + "; signature: "
					+ printSignature(pilotUUID.getSignaturePart()));
			long ms = TimeStampIdGenerator.idToMilliseconds(pilotUUID
					.getLongPart());
			System.out.println("Pilot object was created: "
					+ (new Date(ms)).toString());
			Pilot pilotReturned = (Pilot) container.ext().getByUUID(
					pilotUUID);
			System.out.println("Pilot from UUID: " + pilotReturned);
		} finally {
			container.close();
		}
	}
	// end testGenerateUUID
}
