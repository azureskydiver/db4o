/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.querymode;

import java.io.File;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.QueryEvaluationMode;
import com.db4o.query.Query;
import com.db4o.tools.QueryStats;



public class QueryModesExample {
	public final static String YAPFILENAME="formula1.yap";
	
	public static void main(String[] args) {
		Db4o.configure().objectClass(Pilot.class).objectField("points").indexed(true);
		//testImmediateQueries();
		//testLazyQueries();
		//testSnapshotQueries();
		testLazyConcurrent();
		//testSnapshotConcurrent();
		//testImmediateChanged();
	}
	// end main

	public static void fillUpDB(int pilotCount){
        new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
        	for (int i=0; i<pilotCount;i++){
    			addPilot(db,i);
    		}
		}
        finally {
            db.close();
        }
    }
    // end fillUpDB
	
    private static void addPilot(ObjectContainer db, int points)	{
		Pilot pilot = new Pilot("Tester", points);
		db.set(pilot);
	}
    // end addPilot
   
    public static void testImmediateQueries() {
    	System.out.println("Testing query performance on 10000 pilot objects in Immediate mode");
    	fillUpDB(10000);
    	ObjectContainer db = Db4o.openFile(YAPFILENAME);
    	try {
    		db.ext().configure().queries().evaluationMode(QueryEvaluationMode.IMMEDIATE);
    		QueryStats stats = new QueryStats();       
    		stats.connect(db);
    		Query query = db.query();
    		query.constrain(Pilot.class);
    		query.descend("points").constrain(99).greater();
    		query.execute();
    		long executionTime = stats.executionTime();
    		System.out.println("Query execution time: " + executionTime);
    	} finally {
    		db.close();
    	}
    }
    //end testImmediateQueries
    
    public static void testLazyQueries() {
    	System.out.println("Testing query performance on 10000 pilot objects in Lazy mode");
    	fillUpDB(10000);
    	ObjectContainer db = Db4o.openFile(YAPFILENAME);
    	try {
    		db.ext().configure().queries().evaluationMode(QueryEvaluationMode.LAZY);
    		QueryStats stats = new QueryStats();       
    		stats.connect(db);
    		Query query = db.query();
    		query.constrain(Pilot.class);
    		query.descend("points").constrain(99).greater();
    		query.execute();
    		long executionTime = stats.executionTime();
    		System.out.println("Query execution time: " + executionTime);
    	} finally {
    		db.close();
    	}
    }
    // end testLazyQueries
    
    public static void testLazyConcurrent() {
    	System.out.println("Testing lazy mode with concurrent modifications");
    	fillUpDB(10);
    	ObjectContainer db = Db4o.openFile(YAPFILENAME);
    	try {
    		db.ext().configure().queries().evaluationMode(QueryEvaluationMode.LAZY);
			Query query1 = db.query();
    		query1.constrain(Pilot.class);
    		query1.descend("points").constrain(5).smaller();
    		ObjectSet result1 = query1.execute();

    		Query query2 = db.query();
	    	query2.constrain(Pilot.class);
	    	query2.descend("points").constrain(1);
	    	ObjectSet result2 = query2.execute();
	    	Pilot pilotToDelete = (Pilot)result2.get(0);
	    	System.out.println("Pilot to be deleted: " + pilotToDelete);
	    	db.delete(pilotToDelete);
	    	Pilot pilot = new Pilot("Tester",2);
	    	System.out.println("Pilot to be added: " + pilot);
	    	db.set(pilot);
	    		
	    	System.out.println("Query result after changing from the same transaction");
	    	listResult(result1);
    	} finally {
    		db.close();
    	}
    }
    // end testLazyConcurrent
    
    public static void listResult(ObjectSet result) {
       while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
    
    public static void testSnapshotQueries() {
    	System.out.println("Testing query performance on 10000 pilot objects in Snapshot mode");
    	fillUpDB(10000);
    	ObjectContainer db = Db4o.openFile(YAPFILENAME);
    	try {
    		db.ext().configure().queries().evaluationMode(QueryEvaluationMode.SNAPSHOT);
    		QueryStats stats = new QueryStats();       
    		stats.connect(db);
    		Query query = db.query();
    		query.constrain(Pilot.class);
    		query.descend("points").constrain(99).greater();
    		query.execute();
    		long executionTime = stats.executionTime();
    		System.out.println("Query execution time: " + executionTime);
    	} finally {
    		db.close();
    	}
    }
    // end testSnapshotQueries
   
    public static void testSnapshotConcurrent() {
    	System.out.println("Testing snapshot mode with concurrent modifications");
    	fillUpDB(10);
    	ObjectContainer db = Db4o.openFile(YAPFILENAME);
    	try {
    		db.ext().configure().queries().evaluationMode(QueryEvaluationMode.SNAPSHOT);
			Query query1 = db.query();
    		query1.constrain(Pilot.class);
    		query1.descend("points").constrain(5).smaller();
    		ObjectSet result1 = query1.execute();

    		Query query2 = db.query();
	    	query2.constrain(Pilot.class);
	    	query2.descend("points").constrain(1);
	    	ObjectSet result2 = query2.execute();
	    	Pilot pilotToDelete = (Pilot)result2.get(0);
	    	System.out.println("Pilot to be deleted: " + pilotToDelete);
	    	db.delete(pilotToDelete);
	    	Pilot pilot = new Pilot("Tester",2);
	    	System.out.println("Pilot to be added: " + pilot);
	    	db.set(pilot);
	    		
	    	System.out.println("Query result after changing from the same transaction");
	    	listResult(result1);
    	} finally {
    		db.close();
    	}
    }
    // end testSnapshotConcurrent

    
    public static void testImmediateChanged() {
    	System.out.println("Testing immediate mode with field changes");
    	fillUpDB(10);
    	ObjectContainer db = Db4o.openFile(YAPFILENAME);
    	try {
    		db.ext().configure().queries().evaluationMode(QueryEvaluationMode.IMMEDIATE);
    		Query query1 = db.query();
    		query1.constrain(Pilot.class);
    		query1.descend("points").constrain(5).smaller();
    		ObjectSet result1 = query1.execute();
    		
    		// change field
    		Query query2 = db.query();
    		query2.constrain(Pilot.class);
    		query2.descend("points").constrain(2);
    		ObjectSet result2 = query2.execute();
    		Pilot pilot2 = (Pilot)result2.get(0);
    		pilot2.addPoints(22);
    		db.set(pilot2);
    		listResult(result1);
    	} finally {
    		db.close();
    	}
    }
    // end testImmediateChanged
}
