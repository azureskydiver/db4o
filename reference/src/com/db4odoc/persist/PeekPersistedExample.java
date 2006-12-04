/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.persist;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;


public class PeekPersistedExample {
	public final static String YAPFILENAME="formula1.yap";

	public static void main(String[] args) {
		measureCarTemperature();
	}
	// end main
	
	public static void setObjects(){
		new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Car car = new Car("BMW");
			db.set(car);
		} finally {
			db.close();
		}
	}
	// end setObjects
	
	public static void measureCarTemperature(){
		setObjects();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet result = db.query(Car.class);
			if (result.size() > 0){
				Car car = (Car)result.get(0);
				Car car1  = (Car)db.ext().peekPersisted(car, 5, true);
				Change1 ch1 = new Change1();
				ch1.init(car1);
				Car car2  = (Car)db.ext().peekPersisted(car, 5, true);
				Change2 ch2 = new Change2();
				ch2.init(car2);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {}
				// We can work on the database object at the same time
				car.setModel("BMW M3Coupe");
				db.set(car);
				ch1.stop();
				ch2.stop();
				System.out.println("car1 saved to the database: " + db.ext().isStored(car1));
				System.out.println("car2 saved to the database: " + db.ext().isStored(car1));
				int temperature = (int)((car1.getTemperature() + car2.getTemperature())/2);
				car.setTemperature(temperature);
				db.set(car);
			}
		} finally {
			db.close();
		}
		checkCar();
	}
	// end measureCarTemperature
	
	public static void checkCar(){
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet result = db.query(Car.class);
			listResult(result);
		} finally {
			db.close();
		}
	}
	// end checkCar
	
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
