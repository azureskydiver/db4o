/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.transactions;

import java.io.*;
import com.db4o.*;

public class TransactionExample {
	
	private final static String DB4O_FILE_NAME="reference.db4o";

	public static void main(String[] args) {
        new File(DB4O_FILE_NAME).delete();
        ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
        try {
            storeCarCommit(container);
            container.close();
            container=Db4o.openFile(DB4O_FILE_NAME);
            listAllCars(container);
            storeCarRollback(container);
            container.close();
            container=Db4o.openFile(DB4O_FILE_NAME);
            listAllCars(container);
            carSnapshotRollback(container);
            carSnapshotRollbackRefresh(container);
        }
        finally {
            container.close();
        }
    }
    // end main
    
	private static void storeCarCommit(ObjectContainer container) {
        Pilot pilot=new Pilot("Rubens Barrichello",99);
        Car car=new Car("BMW");
        car.setPilot(pilot);
        container.store(car);
        container.commit();
    }
    // end storeCarCommit

	private static void listAllCars(ObjectContainer container) {
        ObjectSet result=container.queryByExample(Car.class);
        listResult(result);
    }
    // end listAllCars
    
	private static void storeCarRollback(ObjectContainer container) {
        Pilot pilot=new Pilot("Michael Schumacher",100);
        Car car=new Car("Ferrari");
        car.setPilot(pilot);
        container.store(car);
        container.rollback();
    }
    // end storeCarRollback

	private static void carSnapshotRollback(ObjectContainer container) {
        ObjectSet result=container.queryByExample(new Car("BMW"));
        Car car=(Car)result.next();
        car.snapshot();
        container.store(car);
        container.rollback();
        System.out.println(car);
    }
    // end carSnapshotRollback

	private static void carSnapshotRollbackRefresh(ObjectContainer container) {
        ObjectSet result=container.queryByExample(new Car("BMW"));
        Car car=(Car)result.next();
        car.snapshot();
        container.store(car);
        container.rollback();
        container.ext().refresh(car,Integer.MAX_VALUE);
        System.out.println(car);
    }
    // end carSnapshotRollbackRefresh
    
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
