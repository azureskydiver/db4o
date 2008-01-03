/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.clientserver;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;


public class DeepExample {
	private final static String DB4O_FILE_NAME="reference.db4o";
    
	public static void main(String[] args) {
        new File(DB4O_FILE_NAME).delete();
        ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
        try {
            storeCar(container);
            container.close();
            Configuration configuration = setCascadeOnUpdate();
            container=Db4o.openFile(configuration, DB4O_FILE_NAME);
            takeManySnapshots(container);
            container.close();
            container=Db4o.openFile(configuration, DB4O_FILE_NAME);            
            retrieveAllSnapshots(container);
            container.close();
            container=Db4o.openFile(configuration, DB4O_FILE_NAME);
            retrieveSnapshotsSequentially(container);
            retrieveSnapshotsSequentiallyImproved(container);
            container.close();
            configuration = setActivationDepth();
            container=Db4o.openFile(configuration, DB4O_FILE_NAME);
            retrieveSnapshotsSequentially(container);
        }
        finally {
            container.close();
        }
    }
    // end main

    private static void storeCar(ObjectContainer container) {
        Pilot pilot=new Pilot("Rubens Barrichello",99);
        Car car=new Car("BMW");
        car.setPilot(pilot);
        container.store(car);
    }
    // end storeCar

    private static Configuration setCascadeOnUpdate() {
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.objectClass(Car.class).cascadeOnUpdate(true);
    	return configuration;
    }
    // end setCascadeOnUpdate
    
    private static void takeManySnapshots(ObjectContainer container) {
        ObjectSet result=container.queryByExample(Car.class);
        Car car=(Car)result.next();
        for(int i=0;i<5;i++) {
            car.snapshot();
        }
        container.store(car);
    }
    // end takeManySnapshots
    
    private static void retrieveAllSnapshots(ObjectContainer container) {
        ObjectSet result=container.queryByExample(SensorReadout.class);
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end retrieveAllSnapshots

    private static void retrieveSnapshotsSequentially(ObjectContainer container) {
        ObjectSet result=container.queryByExample(Car.class);
        Car car=(Car)result.next();
        SensorReadout readout=car.getHistory();
        while(readout!=null) {
            System.out.println(readout);
            readout=readout.getNext();
        }
    }
    // end retrieveSnapshotsSequentially
    
    private static void retrieveSnapshotsSequentiallyImproved(ObjectContainer container) {
        ObjectSet result=container.queryByExample(Car.class);
        Car car=(Car)result.next();
        SensorReadout readout=car.getHistory();
        while(readout!=null) {
            container.activate(readout,1);
            System.out.println(readout);
            readout=readout.getNext();
        }
    }
    // end retrieveSnapshotsSequentiallyImproved
    
    private static Configuration setActivationDepth() {
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.objectClass(TemperatureSensorReadout.class)
        		.cascadeOnActivate(true);
    	return configuration;
    }
    // end setActivationDepth
}
