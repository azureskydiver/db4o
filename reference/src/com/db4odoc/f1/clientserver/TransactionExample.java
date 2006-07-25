package com.db4odoc.f1.clientserver;

import java.io.*;
import com.db4o.*;
import com.db4odoc.f1.*;


public class TransactionExample extends Util {
    public static void main(String[] args) {
        new File(Util.YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
            storeCarCommit(db);
            db.close();
            db=Db4o.openFile(Util.YAPFILENAME);
            listAllCars(db);
            storeCarRollback(db);
            db.close();
            db=Db4o.openFile(Util.YAPFILENAME);
            listAllCars(db);
            carSnapshotRollback(db);
            carSnapshotRollbackRefresh(db);
        }
        finally {
            db.close();
        }
    }
    
    public static void storeCarCommit(ObjectContainer db) {
        Pilot pilot=new Pilot("Rubens Barrichello",99);
        Car car=new Car("BMW");
        car.setPilot(pilot);
        db.set(car);
        db.commit();
    }

    public static void listAllCars(ObjectContainer db) {
        ObjectSet result=db.get(Car.class);
        listResult(result);
    }
    
    public static void storeCarRollback(ObjectContainer db) {
        Pilot pilot=new Pilot("Michael Schumacher",100);
        Car car=new Car("Ferrari");
        car.setPilot(pilot);
        db.set(car);
        db.rollback();
    }

    public static void carSnapshotRollback(ObjectContainer db) {
        ObjectSet result=db.get(new Car("BMW"));
        Car car=(Car)result.next();
        car.snapshot();
        db.set(car);
        db.rollback();
        System.out.println(car);
    }

    public static void carSnapshotRollbackRefresh(ObjectContainer db) {
        ObjectSet result=db.get(new Car("BMW"));
        Car car=(Car)result.next();
        car.snapshot();
        db.set(car);
        db.rollback();
        db.ext().refresh(car,Integer.MAX_VALUE);
        System.out.println(car);
    }
}
