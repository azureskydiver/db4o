package com.db4o.f1.chapter7;

import java.io.*;
import com.db4o.*;
import com.db4o.f1.*;
import com.db4o.ta.*;


public class TransparentActivationExample extends Util {
    
    public static void main(String[] args) throws Exception{
        // System.out.println(new File(Util.DB4OFILENAME).getCanonicalPath());
        new File(Util.DB4OFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.DB4OFILENAME);
        try {
            storeCarAndSnapshots(db);
            db.close();
            db=Db4o.openFile(Util.DB4OFILENAME);            
            retrieveSnapshotsSequentially(db);
            db.close();
            configureTransparentActivation();
            db=Db4o.openFile(Util.DB4OFILENAME);            
            retrieveSnapshotsSequentially(db);
            db.close();
            db=Db4o.openFile(Util.DB4OFILENAME);            
            demonstrateTransparentActivation(db);
            db.close();
        }
        finally {
            db.close();
        }
    }
    
    public static void configureTransparentActivation(){
        Db4o.configure().add(new TransparentActivationSupport());
    }

    public static void setCascadeOnUpdate() {
        Db4o.configure().objectClass(Car.class).cascadeOnUpdate(true);
    }
    
    public static void storeCarAndSnapshots(ObjectContainer db) {
        Pilot pilot=new Pilot("Kimi Raikkonen",110);
        Car car=new Car("Ferrari");
        car.setPilot(pilot);
        for(int i=0;i<5;i++) {
            car.snapshot();
        }
        db.store(car);
    }
    
    public static void retrieveSnapshotsSequentially(
        ObjectContainer db) {
        ObjectSet result=db.queryByExample(Car.class);
        Car car=(Car)result.next();
        SensorReadout readout=car.getHistory();
        while(readout!=null) {
            System.out.println(readout);
            readout=readout.getNext();
        }
    }
    
    public static void demonstrateTransparentActivation(
        ObjectContainer db)  {
        ObjectSet result=db.queryByExample(Car.class);
        Car car=(Car)result.next();
        
        System.out.println("#getPilotWithoutActivation() before the car is activated");
        System.out.println(car.getPilotWithoutActivation());
        
        System.out.println("calling #getPilot() activates the car object");
        System.out.println(car.getPilot());
        
        System.out.println("#getPilotWithoutActivation() after the car is activated");
        System.out.println(car.getPilotWithoutActivation());
        
    }
    
}
