package com.db4o.f1.chapter8;

import java.io.*;
import com.db4o.*;
import com.db4o.f1.*;
import com.db4o.ta.*;


public class TransparentPersistenceExample extends Util {
    
    public static void main(String[] args) throws Exception{
        new File(Util.DB4OFILENAME).delete();
        configureTransparentPersistence();
        ObjectContainer db=Db4o.openFile(Util.DB4OFILENAME);
        try {
            storeCarAndSnapshots(db);
            db.close();
            db=Db4o.openFile(Util.DB4OFILENAME);            
            modifySnapshotHistory(db);
            db.close();
            db=Db4o.openFile(Util.DB4OFILENAME);            
            readSnapshotHistory(db);
        }
        finally {
            db.close();
        }
    }
    
    public static void configureTransparentPersistence(){
    	Db4o.configure().add(new TransparentPersistenceSupport());
    }
    
    public static void storeCarAndSnapshots(ObjectContainer db) {
        Car car=new Car("Ferrari");
        for(int i=0;i<3;i++) {
            car.snapshot();
        }
        db.store(car);
    }
    
    public static void modifySnapshotHistory(ObjectContainer db) {
    	System.out.println("Read all sensors and modify the description:");
        ObjectSet result=db.queryByExample(Car.class);
        Car car=(Car)result.next();
        SensorReadout readout=car.getHistory();
        while(readout!=null) {
        	System.out.println(readout);
        	readout.setDescription("Modified: " + readout.getDescription());
            readout=readout.getNext();
        }
        db.commit();
    }

    public static void readSnapshotHistory(ObjectContainer db) {
    	System.out.println("Read all modified sensors:");
        ObjectSet result=db.queryByExample(Car.class);
        Car car=(Car)result.next();
        SensorReadout readout=car.getHistory();
        while(readout!=null) {
            System.out.println(readout);
            readout=readout.getNext();
        }
    }

}
