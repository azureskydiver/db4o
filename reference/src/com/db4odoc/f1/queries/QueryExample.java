package com.db4odoc.f1.queries;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Constraint;
import com.db4o.query.Query;


public class QueryExample {
	public final static String YAPFILENAME="formula1.yap";
	
    public static void main(String[] args) {
            storePilot();
            updatePilotWrong();
            updatePilot();
            deletePilot();
            ObjectContainer db=Db4o.openFile(YAPFILENAME);
	    try {
                retrievePilotByName(db);
                retrievePilotByExactPoints(db);
                retrieveByNegation(db);
                retrieveByConjunction(db);
                retrieveByDisjunction(db);
                retrieveByComparison(db);
                retrieveByDefaultFieldValue(db);
                retrieveSorted(db); 
            } finally {
                db.close();
            }
    }
    // end main

    public static void storePilot() {
    	new File(YAPFILENAME).delete();
    	ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
	        Pilot pilot=new Pilot("Michael Schumacher",0);
	        db.set(pilot);
	        System.out.println("Stored "+pilot);
	        // change pilot and resave updated
	        pilot.addPoints(10);
	        db.set(pilot);
	        System.out.println("Stored "+pilot);
        } finally {
        	db.close();
        }
        retrieveAllPilots();
    }
    // end storePilot
    

    public static void updatePilot() {
    	storePilot();
    	ObjectContainer db=Db4o.openFile(YAPFILENAME);
    	try {
	    	// first retrieve the object from the database
	        ObjectSet result=db.get(new Pilot("Michael Schumacher",10));
	        Pilot found=(Pilot)result.next();
	        found.addPoints(10);
	        db.set(found);
	        System.out.println("Added 10 points for "+found);
    } finally {
    	db.close();
    }
    retrieveAllPilots();
    }
    // end updatePilot

    public static void updatePilotWrong() {
    	storePilot();
    	ObjectContainer db=Db4o.openFile(YAPFILENAME);
    	try {
    	// Even completely identical Pilot object
    	// won't work for update of the saved pilot
        Pilot pilot = new Pilot("Michael Schumacher",10);
        pilot.addPoints(10);
        db.set(pilot);
        System.out.println("Added 10 points for "+pilot);
        } finally {
    		db.close();
    	}
    	retrieveAllPilots();
    }
    // end updatePilotWrong
    
    public static void deletePilot() {
    	storePilot();
    	ObjectContainer db=Db4o.openFile(YAPFILENAME);
    	try {
//    	 first retrieve the object from the database
        ObjectSet result=db.get(new Pilot("Michael Schumacher",10));
        Pilot found=(Pilot)result.next();
        db.delete(found);
        System.out.println("Deleted "+found);
        } finally {
    		db.close();
    	}
        retrieveAllPilots();
    }
    // end deletePilot

    
    public static void retrieveAllPilots() {
    	ObjectContainer db=Db4o.openFile(YAPFILENAME);
    	try {
	        Query query=db.query();
	        query.constrain(Pilot.class);
	        ObjectSet result=query.execute();
	        listResult(result);
    	} finally {
    		db.close();
    	}
    }
    // end retrieveAllPilots

    public static void retrievePilotByName(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("name").constrain("Michael Schumacher");
        ObjectSet result=query.execute();
        listResult(result);
    }
    // end retrievePilotByName
    
    public static void retrievePilotByExactPoints(
            ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("points").constrain(new Integer(100));
        ObjectSet result=query.execute();
        listResult(result);
    }
    // end retrievePilotByExactPoints

    public static void retrieveByNegation(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("name").constrain("Michael Schumacher").not();
        ObjectSet result=query.execute();
        listResult(result);
    }
    // end retrieveByNegation

    public static void retrieveByConjunction(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        Constraint constr=query.descend("name")
                .constrain("Michael Schumacher");
        query.descend("points")
                .constrain(new Integer(99)).and(constr);
        ObjectSet result=query.execute();
        listResult(result);
    }
    // end retrieveByConjunction

    public static void retrieveByDisjunction(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        Constraint constr=query.descend("name")
                .constrain("Michael Schumacher");
        query.descend("points")
                .constrain(new Integer(99)).or(constr);
        ObjectSet result=query.execute();
        listResult(result);
    }
    // end retrieveByDisjunction

    public static void retrieveByComparison(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("points")
                .constrain(new Integer(99)).greater();
        ObjectSet result=query.execute();
        listResult(result);
    }
    // end retrieveByComparison

    public static void retrieveByDefaultFieldValue(
                    ObjectContainer db) {
        Pilot somebody=new Pilot("Somebody else",0);
        db.set(somebody);
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("points").constrain(new Integer(0));
        ObjectSet result=query.execute();
        listResult(result);
        db.delete(somebody);
    }
    // end retrieveByDefaultFieldValue
    
    public static void retrieveSorted(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("name").orderAscending();
        ObjectSet result=query.execute();
        listResult(result);
        query.descend("name").orderDescending();
        result=query.execute();
        listResult(result);
    }
    // end retrieveSorted

    public static void clearDatabase(ObjectContainer db) {
        ObjectSet result=db.get(Pilot.class);
        while(result.hasNext()) {
            db.delete(result.next());
        }
    }
    // end clearDatabase
    
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
