package com.db4o.f1.chapter3;

import java.io.*;
import java.util.*;
import com.db4o.*;
import com.db4o.f1.*;
import com.db4o.query.*;


public class CollectionsExample extends Util {
    public static void main(String[] args) {
        new File(Util.DB4OFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.DB4OFILENAME);
        try {
            storeFirstCar(db);
            storeSecondCar(db);
            retrieveAllSensorReadout(db);
            retrieveSensorReadoutQBE(db);
            retrieveCarQBE(db);
            retrieveCollections(db);
            retrieveArrays(db);
            retrieveAllSensorReadoutNative(db);
            retrieveSensorReadoutNative(db);
            retrieveCarNative(db);
            retrieveSensorReadoutQuery(db);
            retrieveCarQuery(db);
            db.close();
            updateCarPart1();
            db=Db4o.openFile(Util.DB4OFILENAME);
            updateCarPart2(db);
            updateCollection(db);
            db.close();
            deleteAllPart1();
            db=Db4o.openFile(Util.DB4OFILENAME);
            deleteAllPart2(db);
        }
        finally {
            db.close();
        }
    }

    public static void storeFirstCar(ObjectContainer db) {
        Car car1=new Car("Ferrari");
        Pilot pilot1=new Pilot("Michael Schumacher",100);
        car1.setPilot(pilot1);
        db.store(car1);
    }
    
    public static void storeSecondCar(ObjectContainer db) {
        Pilot pilot2=new Pilot("Rubens Barrichello",99);
        Car car2=new Car("BMW");
        car2.setPilot(pilot2);
        car2.snapshot();
        car2.snapshot();
        db.store(car2);
    }
    
    public static void retrieveAllSensorReadout(
                ObjectContainer db) {
        SensorReadout proto=new SensorReadout(null,null,null);
        ObjectSet results=db.queryByExample(proto);
        listResult(results);
    }

    public static void retrieveAllSensorReadoutNative(
            ObjectContainer db) {
    	List<SensorReadout> results = db.query(new Predicate<SensorReadout>() {
    		public boolean match(SensorReadout candidate){
    			return true;
    		}
    	});
    	listResult(results);
    }
    
    public static void retrieveSensorReadoutQBE(
                ObjectContainer db) {
        SensorReadout proto=new SensorReadout(
                new double[]{0.3,0.1},null,null);
        ObjectSet results=db.queryByExample(proto);
        listResult(results);
    }

    public static void retrieveSensorReadoutNative(
            ObjectContainer db) {
    	List<SensorReadout> results = db.query(new Predicate<SensorReadout>() {
    		public boolean match(SensorReadout candidate){
    			return Arrays.binarySearch(candidate.getValues(), 0.3) >= 0 
    				&& Arrays.binarySearch(candidate.getValues(), 1.0) >= 0;
    		}
    	});
    	listResult(results);
    }

    public static void retrieveCarQBE(ObjectContainer db) {
        SensorReadout protoreadout=new SensorReadout(
                new double[]{0.6,0.2},null,null);
        List protohistory=new ArrayList();
        protohistory.add(protoreadout);
        Car protocar=new Car(null,protohistory);
        ObjectSet result=db.queryByExample(protocar);
        listResult(result);
    }

    public static void retrieveCarNative(
            ObjectContainer db) {
    	List<Car> results = db.query(new Predicate<Car>() {
    		public boolean match(Car candidate){
    			List history = candidate.getHistory();
    			for(int i = 0; i < history.size(); i++){
    				SensorReadout readout = (SensorReadout)history.get(i);
    				if( Arrays.binarySearch(readout.getValues(), 0.6) >= 0 ||
    				Arrays.binarySearch(readout.getValues(), 0.2) >= 0)
    					return true;
    			}
    			return false;
    		}
    	});
    	listResult(results);
    }
    
    public static void retrieveCollections(ObjectContainer db) {
        ObjectSet result=db.queryByExample(new ArrayList());
        listResult(result);
    }

    public static void retrieveArrays(ObjectContainer db) {
        ObjectSet result=db.queryByExample(new double[]{0.6,0.4});
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
        Db4o.configure().objectClass(Car.class).cascadeOnUpdate(true);
    }

    public static void updateCarPart2(ObjectContainer db) {
    	List<Car> results = db.query(new Predicate<Car>() {
    		public boolean match(Car candidate){
    			return true;
    		}
    	});
    	Car car=results.get(0);
        car.snapshot();
        db.store(car);
        retrieveAllSensorReadoutNative(db);
    }
    
    public static void updateCollection(ObjectContainer db) {
    	ObjectSet<Car> results = db.query(new Predicate<Car>() {
    		public boolean match(Car candidate){
    			return true;
    		}
    	});
        Car car =(Car)results.next();
        car.getHistory().remove(0);
        db.store(car.getHistory());
    	results = db.query(new Predicate<Car>() {
    		public boolean match(Car candidate){
    			return true;
    		}
    	});
        while(results.hasNext()) {
            car=results.next();
            for (int idx=0;idx<car.getHistory().size();idx++) {
                System.out.println(car.getHistory().get(idx));
            }
        }
    }
    
    public static void deleteAllPart1() {
        Db4o.configure().objectClass(Car.class)
        		.cascadeOnDelete(true);
    }
    
    public static void deleteAllPart2(ObjectContainer db) {
    	ObjectSet<Car> cars = db.query(new Predicate<Car>() {
    		public boolean match(Car candidate){
    			return true;
    		}
    	});
        while(cars.hasNext()) {
            db.delete(cars.next());
        }
    	ObjectSet<SensorReadout> readouts = db.query(new Predicate<SensorReadout>() {
    		public boolean match(SensorReadout candidate){
    			return true;
    		}
    	});
        while(readouts.hasNext()) {
            db.delete(readouts.next());
        }
    }
}
