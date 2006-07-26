package com.db4o.f1.chapter21;

import java.io.File;
import java.util.Calendar;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.f1.Util;
import com.db4o.query.Query;


public class IndexedExample extends Util {
    public static void noIndex() {
    	ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
    		Query query = db.query();
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
            db.close();
        }
    }
    
    public static void fillUpDB(){
        new File(Util.YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
        	for (int i=0; i<10000;i++){
    			AddCar(db,i);
    		}
		}
        finally {
            db.close();
        }
    }
  
    public static void pilotIndex() {
    	Db4o.configure().objectClass(Car.class).objectField("pilot").indexed(true);
    	Db4o.configure().objectClass(Pilot.class).objectField("points").indexed(false);
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
    		Query query = db.query();
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
            db.close();
        }
    }
   
    public static void pointsIndex() {
    	Db4o.configure().objectClass(Car.class).objectField("pilot").indexed(false);
    	Db4o.configure().objectClass(Pilot.class).objectField("points").indexed(true);
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
    		Query query = db.query();
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
            db.close();
        }
    }
    
    
    public static void fullIndex() {
    	Db4o.configure().objectClass(Car.class).objectField("pilot").indexed(true);
    	Db4o.configure().objectClass(Pilot.class).objectField("points").indexed(true);
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
    		Query query = db.query();
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
            db.close();
        }
    }

    
    private static void AddCar(ObjectContainer db, int points)
	{
		Car car = new Car("BMW");
		car.setPilot(new Pilot("Tester", points));
		db.set(car);
	}
    
    
}
