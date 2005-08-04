package com.db4o.f1.chapter3;

import java.io.*;
import java.util.*;
import com.db4o.*;
import com.db4o.f1.*;
import com.db4o.query.*;


public class CollectionsExample extends Util {
    public static void main(String[] args) {
        new File(Util.YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
            storeFirstCar(db);
            storeSecondCar(db);
            retrieveAllSensorReadouts(db);
            retrieveSensorReadoutQBE(db);
            retrieveCarQBE(db);
            retrieveCollections(db);
            retrieveArrays(db);
            retrieveSensorReadoutQuery(db);
            retrieveCarQuery(db);
            db.close();
            updateCarPart1();
            db=Db4o.openFile(Util.YAPFILENAME);
            updateCarPart2(db);
            updateCollection(db);
            db.close();
            deleteAllPart1();
            db=Db4o.openFile(Util.YAPFILENAME);
            deleteAllPart2(db);
            retrieveAllSensorReadouts(db);
        }
        finally {
            db.close();
        }
    }

    public static void storeFirstCar(ObjectContainer db) {
        Car car1=new Car("Ferrari");
        Pilot pilot1=new Pilot("Michael Schumacher",100);
        car1.setPilot(pilot1);
        db.set(car1);
    }
    
    public static void storeSecondCar(ObjectContainer db) {
        Pilot pilot2=new Pilot("Rubens Barrichello",99);
        Car car2=new Car("BMW");
        car2.setPilot(pilot2);
        car2.snapshot();
        car2.snapshot();
        db.set(car2);
    }
    
    public static void retrieveAllSensorReadouts(
                ObjectContainer db) {
        SensorReadout proto=new SensorReadout(null,null,null);
        ObjectSet result=db.get(proto);
        listResult(result);
    }

    public static void retrieveSensorReadoutQBE(
                ObjectContainer db) {
        SensorReadout proto=new SensorReadout(
                new double[]{0.3,0.1},null,null);
        ObjectSet result=db.get(proto);
        listResult(result);
    }

    public static void retrieveCarQBE(ObjectContainer db) {
        SensorReadout protoreadout=new SensorReadout(
                new double[]{0.6,0.2},null,null);
        List protohistory=new ArrayList();
        protohistory.add(protoreadout);
        Car protocar=new Car(null,protohistory);
        ObjectSet result=db.get(protocar);
        listResult(result);
    }

    public static void retrieveCollections(ObjectContainer db) {
        ObjectSet result=db.get(new ArrayList());
        listResult(result);
    }

    public static void retrieveArrays(ObjectContainer db) {
        ObjectSet result=db.get(new double[]{0.6,0.4});
        listResult(result);
    }

    public static void retrieveSensorReadoutQuery(
                ObjectContainer db) {
        Query query=db.query();
        query.constrain(SensorReadout.class);
        Query valuequery=query.descend("values");
        valuequery.constrain(new Double(0.3));
        valuequery.constrain(new Double(0.1));
        ObjectSet result=query.execute();
        listResult(result);
    }

    public static void retrieveCarQuery(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Car.class);
        Query historyquery=query.descend("history");
        historyquery.constrain(SensorReadout.class);
        Query valuequery=historyquery.descend("values");
        valuequery.constrain(new Double(0.3));
        valuequery.constrain(new Double(0.1));
        ObjectSet result=query.execute();
        listResult(result);
    }

    public static void updateCarPart1() {
        Db4o.configure().objectClass(Car.class)
        		.cascadeOnUpdate(true);
    }

    public static void updateCarPart2(ObjectContainer db) {
        ObjectSet result=db.get(new Car("BMW",null));
        Car car=(Car)result.next();
        car.snapshot();
        db.set(car);
        retrieveAllSensorReadouts(db);
    }
    
    public static void updateCollection(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Car.class);
        ObjectSet result=query.descend("history").execute();
        List coll=(List)result.next();
        coll.remove(0);
        db.set(coll);
        Car proto=new Car(null,null);
        result=db.get(proto);
        while(result.hasNext()) {
            Car car=(Car)result.next();
            for (int idx=0;idx<car.getHistory().length;idx++) {
                System.out.println(car.getHistory()[idx]);
            }
        }
    }
    
    public static void deleteAllPart1() {
        Db4o.configure().objectClass(Car.class)
        		.cascadeOnDelete(true);
    }
    
    public static void deleteAllPart2(ObjectContainer db) {
        ObjectSet result=db.get(new Car(null,null));
        while(result.hasNext()) {
            db.delete(result.next());
        }
        ObjectSet readouts=db.get(
                new SensorReadout(null,null,null));
        while(readouts.hasNext()) {
            db.delete(readouts.next());
        }
    }
}
