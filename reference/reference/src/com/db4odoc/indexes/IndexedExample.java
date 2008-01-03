/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.indexes;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.query.Query;


public class IndexedExample {
	
	private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void main(String[] args){
		IndexedExample.fillUpDB();
        IndexedExample.noIndex();
        IndexedExample.fullIndex();
        IndexedExample.pilotIndex();
        IndexedExample.pointsIndex();
	}
	// end main
	
    private static void noIndex() {
    	ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
        try {
    		Query query = container.query();
			query.constrain(Car.class);
			query.descend("pilot").descend("points").constrain(new Integer(99));

			long t1 = System.currentTimeMillis();
			ObjectSet  result = query.execute();
			long t2 = System.currentTimeMillis();
			long  diff = t2 - t1;
			System.out.println("Test 1: no indexes");
			System.out.println("Execution time="+diff + " ms");
			listResult(result);
        }
        finally {
            container.close();
        }
    }
    // end noIndex
    
    private static void fillUpDB(){
        new File(DB4O_FILE_NAME).delete();
        ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
        try {
        	for (int i=0; i<10000;i++){
    			AddCar(container,i);
    		}
		}
        finally {
            container.close();
        }
    }
    // end fillUpDB
  
    private static void pilotIndex() {
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.objectClass(Car.class).objectField("pilot").indexed(true);
    	configuration.objectClass(Pilot.class).objectField("points").indexed(false);
        ObjectContainer container=Db4o.openFile(configuration, DB4O_FILE_NAME);
        try {
    		Query query = container.query();
			query.constrain(Car.class);
			query.descend("pilot").descend("points").constrain(new Integer(99));

			long t1 = System.currentTimeMillis();
			ObjectSet  result = query.execute();
			long t2 = System.currentTimeMillis();
			long  diff = t2 - t1;
			System.out.println("Test 3: index on pilot");
			System.out.println("Execution time="+diff + " ms");
			listResult(result);
        }
        finally {
            container.close();
        }
    }
    // end pilotIndex
   
    private static void pointsIndex() {
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.objectClass(Car.class).objectField("pilot").indexed(false);
    	configuration.objectClass(Pilot.class).objectField("points").indexed(true);
        ObjectContainer container=Db4o.openFile(configuration, DB4O_FILE_NAME);
        try {
    		Query query = container.query();
			query.constrain(Car.class);
			query.descend("pilot").descend("points").constrain(new Integer(99));

			long t1 = System.currentTimeMillis();
			ObjectSet  result = query.execute();
			long t2 = System.currentTimeMillis();
			long  diff = t2 - t1;
			System.out.println("Test 4: index on points");
			System.out.println("Execution time="+diff + " ms");
			listResult(result);
        }
        finally {
            container.close();
        }
    }
    // end pointsIndex
    
    
    private static void fullIndex() {
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.objectClass(Car.class).objectField("pilot").indexed(true);
    	configuration.objectClass(Pilot.class).objectField("points").indexed(true);
        ObjectContainer container=Db4o.openFile(configuration, DB4O_FILE_NAME);
        try {
    		Query query = container.query();
			query.constrain(Car.class);
			query.descend("pilot").descend("points").constrain(new Integer(99));

			long t1 = System.currentTimeMillis();
			ObjectSet  result = query.execute();
			long t2 = System.currentTimeMillis();
			long  diff = t2 - t1;
			System.out.println("Test 2: index on pilot and points");
			System.out.println("Execution time="+diff + " ms");
			listResult(result);
        }
        finally {
            container.close();
        }
    }
    // end fullIndex

    
    private static void AddCar(ObjectContainer container, int points)
	{
		Car car = new Car("BMW");
		car.setPilot(new Pilot("Tester", points));
		container.store(car);
	}
    // end AddCar
    
    private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
    
}
