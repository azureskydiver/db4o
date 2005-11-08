package com.db4o.f1.chapter4;

import java.io.*;
import java.util.Arrays;

import com.db4o.*;
import com.db4o.f1.*;
import com.db4o.query.*;


public class InheritanceExample extends Util {
    public static void main(String[] args) {
        new File(Util.YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
            storeFirstCar(db);
            storeSecondCar(db);
            retrieveTemperatureReadoutsQBE(db);
            retrieveAllSensorReadoutsQBE(db);
            retrieveAllSensorReadoutsQBEAlternative(db);
            retrieveAllSensorReadoutsQuery(db);
            retrieveAllObjectsQBE(db);
            retrieveAllSensorReadoutsNative(db);
            retrieveAllObjectsNative(db);
            deleteAllObjectsNative(db);
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

    public static void retrieveAllSensorReadoutsQBE(
            ObjectContainer db) {
        SensorReadout proto=new SensorReadout(null,null,null);
        ObjectSet result=db.get(proto);
        listResult(result);
    }

    public static void retrieveTemperatureReadoutsQBE(
            ObjectContainer db) {
        SensorReadout proto=
            new TemperatureSensorReadout(null,null,null,0.0);
        ObjectSet result=db.get(proto);
        listResult(result);
    }

    public static void retrieveAllSensorReadoutsQBEAlternative(
            ObjectContainer db) {
        ObjectSet result=db.get(SensorReadout.class);
        listResult(result);
    }

    public static void retrieveAllSensorReadoutsQuery(
            ObjectContainer db) {
        Query query=db.query();
        query.constrain(SensorReadout.class);
        ObjectSet result=query.execute();
        listResult(result);
    }
    
    public static void retrieveAllObjectsQBE(ObjectContainer db) {
        ObjectSet result=db.get(new Object());
        listResult(result);
    }

    public static void retrieveAllSensorReadoutsNative(
            ObjectContainer db) {
    	ObjectSet results = db.query(new Predicate() {
    	    public boolean match(SensorReadout candidate){
    	        return true;
    	    }
    	});
    	listResult(results);
    }
    
    public static void retrieveAllObjectsNative(ObjectContainer db) {
        ObjectSet result=db.query(new Predicate(){
            public boolean match(Object candidate){
                return true;
            }
        });
        listResult(result);
    }
    
    public static void deleteAllObjectsNative(ObjectContainer db) {
        ObjectSet result=db.query(new Predicate(){
            public boolean match(Object candidate){
                return true;
            }
        });
        while(result.hasNext()) {
            db.delete(result.next());
        }
    }
}
