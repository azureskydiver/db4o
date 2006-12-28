package com.db4odoc.clientserver;

import java.io.*;
import com.db4o.*;


public class DeepExample {
	public final static String YAPFILENAME="formula1.yap";
    public static void main(String[] args) {
        new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
            storeCar(db);
            db.close();
            setCascadeOnUpdate();
            db=Db4o.openFile(YAPFILENAME);
            takeManySnapshots(db);
            db.close();
            db=Db4o.openFile(YAPFILENAME);            
            retrieveAllSnapshots(db);
            db.close();
            db=Db4o.openFile(YAPFILENAME);
            retrieveSnapshotsSequentially(db);
            retrieveSnapshotsSequentiallyImproved(db);
            db.close();
            setActivationDepth();
            db=Db4o.openFile(YAPFILENAME);
            retrieveSnapshotsSequentially(db);
        }
        finally {
            db.close();
        }
    }
    // end main

    public static void storeCar(ObjectContainer db) {
        Pilot pilot=new Pilot("Rubens Barrichello",99);
        Car car=new Car("BMW");
        car.setPilot(pilot);
        db.set(car);
    }
    // end storeCar

    public static void setCascadeOnUpdate() {
        Db4o.configure().objectClass(Car.class).cascadeOnUpdate(true);
    }
    // end setCascadeOnUpdate
    
    public static void takeManySnapshots(ObjectContainer db) {
        ObjectSet result=db.get(Car.class);
        Car car=(Car)result.next();
        for(int i=0;i<5;i++) {
            car.snapshot();
        }
        db.set(car);
    }
    // end takeManySnapshots
    
    public static void retrieveAllSnapshots(ObjectContainer db) {
        ObjectSet result=db.get(SensorReadout.class);
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end retrieveAllSnapshots

    public static void retrieveSnapshotsSequentially(ObjectContainer db) {
        ObjectSet result=db.get(Car.class);
        Car car=(Car)result.next();
        SensorReadout readout=car.getHistory();
        while(readout!=null) {
            System.out.println(readout);
            readout=readout.getNext();
        }
    }
    // end retrieveSnapshotsSequentially
    
    public static void retrieveSnapshotsSequentiallyImproved(ObjectContainer db) {
        ObjectSet result=db.get(Car.class);
        Car car=(Car)result.next();
        SensorReadout readout=car.getHistory();
        while(readout!=null) {
            db.activate(readout,1);
            System.out.println(readout);
            readout=readout.getNext();
        }
    }
    // end retrieveSnapshotsSequentiallyImproved
    
    public static void setActivationDepth() {
        Db4o.configure().objectClass(TemperatureSensorReadout.class)
        		.cascadeOnActivate(true);
    }
    // end setActivationDepth
}
