package com.db4odoc.indexes;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;


public class IndexedExample {
	public final static String YAPFILENAME="formula1.yap";
	public static void main(String[] args){
		IndexedExample.fillUpDB();
        IndexedExample.noIndex();
        IndexedExample.fullIndex();
        IndexedExample.pilotIndex();
        IndexedExample.pointsIndex();
	}
	// end main
	
    public static void noIndex() {
    	ObjectContainer db=Db4o.openFile(YAPFILENAME);
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
    // end noIndex
    
    public static void fillUpDB(){
        new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
        	for (int i=0; i<10000;i++){
    			AddCar(db,i);
    		}
		}
        finally {
            db.close();
        }
    }
    // end fillUpDB
  
    public static void pilotIndex() {
    	Db4o.configure().objectClass(Car.class).objectField("pilot").indexed(true);
    	Db4o.configure().objectClass(Pilot.class).objectField("points").indexed(false);
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
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
    // end pilotIndex
   
    public static void pointsIndex() {
    	Db4o.configure().objectClass(Car.class).objectField("pilot").indexed(false);
    	Db4o.configure().objectClass(Pilot.class).objectField("points").indexed(true);
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
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
    // end pointsIndex
    
    
    public static void fullIndex() {
    	Db4o.configure().objectClass(Car.class).objectField("pilot").indexed(true);
    	Db4o.configure().objectClass(Pilot.class).objectField("points").indexed(true);
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
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
    // end fullIndex

    
    private static void AddCar(ObjectContainer db, int points)
	{
		Car car = new Car("BMW");
		car.setPilot(new Pilot("Tester", points));
		db.set(car);
	}
    // end AddCar
    
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
    
}
