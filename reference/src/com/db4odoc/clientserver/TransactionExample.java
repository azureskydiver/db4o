package com.db4odoc.clientserver;

import java.io.*;
import com.db4o.*;


public class TransactionExample {
	public final static String YAPFILENAME="formula1.yap";
    public static void main(String[] args) {
        new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
            storeCarCommit(db);
            db.close();
            db=Db4o.openFile(YAPFILENAME);
            listAllCars(db);
            storeCarRollback(db);
            db.close();
            db=Db4o.openFile(YAPFILENAME);
            listAllCars(db);
            carSnapshotRollback(db);
            carSnapshotRollbackRefresh(db);
        }
        finally {
            db.close();
        }
    }
    // end main
    
    public static void storeCarCommit(ObjectContainer db) {
        Pilot pilot=new Pilot("Rubens Barrichello",99);
        Car car=new Car("BMW");
        car.setPilot(pilot);
        db.set(car);
        db.commit();
    }
    // end storeCarCommit

    public static void listAllCars(ObjectContainer db) {
        ObjectSet result=db.get(Car.class);
        listResult(result);
    }
    // end listAllCars
    
    public static void storeCarRollback(ObjectContainer db) {
        Pilot pilot=new Pilot("Michael Schumacher",100);
        Car car=new Car("Ferrari");
        car.setPilot(pilot);
        db.set(car);
        db.rollback();
    }
    // end storeCarRollback

    public static void carSnapshotRollback(ObjectContainer db) {
        ObjectSet result=db.get(new Car("BMW"));
        Car car=(Car)result.next();
        car.snapshot();
        db.set(car);
        db.rollback();
        System.out.println(car);
    }
    // end carSnapshotRollback

    public static void carSnapshotRollbackRefresh(ObjectContainer db) {
        ObjectSet result=db.get(new Car("BMW"));
        Car car=(Car)result.next();
        car.snapshot();
        db.set(car);
        db.rollback();
        db.ext().refresh(car,Integer.MAX_VALUE);
        System.out.println(car);
    }
    // end carSnapshotRollbackRefresh
    
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
