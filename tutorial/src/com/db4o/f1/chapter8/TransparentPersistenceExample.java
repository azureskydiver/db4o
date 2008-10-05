package com.db4o.f1.chapter8;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.f1.*;
import com.db4o.ta.*;

public class TransparentPersistenceExample extends Util {

	final static String DB4OFILENAME = System.getProperty("user.home") + "/formula1.db4o";

	public static void main(String[] args) throws Exception {
		new File(DB4OFILENAME).delete();
		storeCarAndSnapshots();
		modifySnapshotHistory();
		readSnapshotHistory();

	}

	public static void storeCarAndSnapshots() {
		Configuration config = Db4oEmbedded.newConfiguration();
		config.add(new TransparentPersistenceSupport());
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);
		Car car = new Car("Ferrari");
		for (int i = 0; i < 3; i++) {
			car.snapshot();
		}
		db.store(car);
		db.close();
	}

	public static void modifySnapshotHistory() {
		Configuration config = Db4oEmbedded.newConfiguration();
		config.add(new TransparentPersistenceSupport());
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);

		System.out.println("Read all sensors and modify the description:");
		ObjectSet result = db.queryByExample(Car.class);
		if (result.hasNext()) {
			Car car = (Car) result.next();
			SensorReadout readout = car.getHistory();
			while (readout != null) {
				System.out.println(readout);
				readout.setDescription("Modified: " + readout.getDescription());
				readout = readout.getNext();
			}
			db.commit();
		}
		db.close();
	}

	public static void readSnapshotHistory() {
		Configuration config = Db4oEmbedded.newConfiguration();
		config.add(new TransparentPersistenceSupport());
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);

		System.out.println("Read all modified sensors:");
		ObjectSet result = db.queryByExample(Car.class);
		if (result.hasNext()) {
			Car car = (Car) result.next();
			SensorReadout readout = car.getHistory();
			while (readout != null) {
				System.out.println(readout);
				readout = readout.getNext();
			}
		}
		db.close();
	}

}
