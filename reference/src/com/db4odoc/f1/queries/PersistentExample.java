package com.db4odoc.f1.queries;


import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class PersistentExample {
	public final static String YAPFILENAME="formula1.yap";
	
    public static void main(String[] args) {
        new File(YAPFILENAME).delete();
        accessDb4o();
        new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
            storeFirstPilot(db);
            storeSecondPilot(db);
            retrieveAllPilots(db);
            retrievePilotByName(db);
            retrievePilotByExactPoints(db);
            updatePilot(db);
            deleteFirstPilotByName(db);
            deleteSecondPilotByName(db);
        }
        finally {
            db.close();
        }
    }
    // end main
    
    public static void accessDb4o() {
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
            // do something with db4o
        }
        finally {
            db.close();
        }
    }
    // end accessDb4o
    
    public static void storeFirstPilot(ObjectContainer db) {
        Pilot pilot1=new Pilot("Michael Schumacher",100);
        db.set(pilot1);
        System.out.println("Stored "+pilot1);
    }
    // end storeFirstPilot

    public static void storeSecondPilot(ObjectContainer db) {
        Pilot pilot2=new Pilot("Rubens Barrichello",99);
        db.set(pilot2);
        System.out.println("Stored "+pilot2);
    }
    // end storeSecondPilot

    public static void retrieveAllPilotQBE(ObjectContainer db) {
        Pilot proto=new Pilot(null,0);
        ObjectSet result=db.get(proto);
        listResult(result);
    }
    // end retrieveAllPilotQBE
    
    public static void retrieveAllPilots(ObjectContainer db) {
        ObjectSet result=db.get(Pilot.class);
        listResult(result);
    }
    // end retrieveAllPilots

    public static void retrievePilotByName(ObjectContainer db) {
        Pilot proto=new Pilot("Michael Schumacher",0);
        ObjectSet result=db.get(proto);
        listResult(result);
    }
    // end retrievePilotByName
    
    public static void retrievePilotByExactPoints(ObjectContainer db) {
        Pilot proto=new Pilot(null,100);
        ObjectSet result=db.get(proto);
        listResult(result);
    }
    // end retrievePilotByExactPoints

    public static void updatePilot(ObjectContainer db) {
        ObjectSet result=db.get(new Pilot("Michael Schumacher",0));
        Pilot found=(Pilot)result.next();
        found.addPoints(11);
        db.set(found);
        System.out.println("Added 11 points for "+found);
        retrieveAllPilots(db);
    }
    // end updatePilot

    public static void deleteFirstPilotByName(ObjectContainer db) {
        ObjectSet result=db.get(new Pilot("Michael Schumacher",0));
        Pilot found=(Pilot)result.next();
        db.delete(found);
        System.out.println("Deleted "+found);
        retrieveAllPilots(db);
    }
    // end deleteFirstPilotByName

    public static void deleteSecondPilotByName(ObjectContainer db) {
        ObjectSet result=db.get(new Pilot("Rubens Barrichello",0));
        Pilot found=(Pilot)result.next();
        db.delete(found);
        System.out.println("Deleted "+found);
        retrieveAllPilots(db);
    }
    // end deleteSecondPilotByName
    
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
