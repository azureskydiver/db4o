package com.db4odoc.f1.clientserver;

import java.io.*;
import com.db4o.*;
import com.db4odoc.f1.*;


public class DeepExample extends Util {
    public static void main(String[] args) {
        new File(Util.YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
            storeCar(db);
            db.close();
            setCascadeOnUpdate();
            db=Db4o.openFile(Util.YAPFILENAME);
            takeManySnapshots(db);
            db.close();
            db=Db4o.openFile(Util.YAPFILENAME);            
            retrieveAllSnapshots(db);
            db.close();
            db=Db4o.openFile(Util.YAPFILENAME);
            retrieveSnapshotsSequentially(db);
            retrieveSnapshotsSequentiallyImproved(db);
            db.close();
            setActivationDepth();
            db=Db4o.openFile(Util.YAPFILENAME);
            retrieveSnapshotsSequentially(db);
        }
        finally {
            db.close();
        }
    }

    public static void storeCar(ObjectContainer db) {
        Pilot pilot=new Pilot("Rubens Barrichello",99);
        Car car=new Car("BMW");
        car.setPilot(pilot);
        db.set(car);
    }

    public static void setCascadeOnUpdate() {
        Db4o.configure().objectClass(Car.class).cascadeOnUpdate(true);
    }
    
    public static void takeManySnapshots(ObjectContainer db) {
        ObjectSet result=db.get(Car.class);
        Car car=(Car)result.next();
        for(int i=0;i<5;i++) {
            car.snapshot();
        }
        db.set(car);
    }
    
    public static void retrieveAllSnapshots(ObjectContainer db) {
        ObjectSet result=db.get(SensorReadout.class);
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }

    public static void retrieveSnapshotsSequentially(
            ObjectContainer db) {
        ObjectSet result=db.get(Car.class);
        Car car=(Car)result.next();
        SensorReadout readout=car.getHistory();
        while(readout!=null) {
            System.out.println(readout);
            readout=readout.getNext();
        }
    }
    
    public static void retrieveSnapshotsSequentiallyImproved(
            ObjectContainer db) {
        ObjectSet result=db.get(Car.class);
        Car car=(Car)result.next();
        SensorReadout readout=car.getHistory();
        while(readout!=null) {
            db.activate(readout,1);
            System.out.println(readout);
            readout=readout.getNext();
        }
    }
    
    public static void setActivationDepth() {
        Db4o.configure().objectClass(TemperatureSensorReadout.class)
        		.cascadeOnActivate(true);
    }
}
