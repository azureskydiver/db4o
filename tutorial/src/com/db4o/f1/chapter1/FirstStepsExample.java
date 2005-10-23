package com.db4o.f1.chapter1;


import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.f1.*;
import com.db4o.query.*;


public class FirstStepsExample extends Util {    
    public static void main(String[] args) {
        new File(Util.YAPFILENAME).delete();
        accessDb4o();
        new File(Util.YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
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
    
    public static void accessDb4o() {
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
            // do something with db4o
        }
        finally {
            db.close();
        }
    }
    
    public static void storeFirstPilot(ObjectContainer db) {
        Pilot pilot1=new Pilot("Michael Schumacher",100);
        db.set(pilot1);
        System.out.println("Stored "+pilot1);
    }

    public static void storeSecondPilot(ObjectContainer db) {
        Pilot pilot2=new Pilot("Rubens Barrichello",99);
        db.set(pilot2);
        System.out.println("Stored "+pilot2);
    }

    public static void retrieveAllPilots(ObjectContainer db) {
        Pilot proto=new Pilot(null,0);
        List result = db.query(
        	new Predicate() {
        		public boolean match(Pilot pilot){return true;}
        	});
        listResult(result);
    }
    
    public static void retrievePilotByName(ObjectContainer db) {
        List result=db.query(
        	new Predicate() {
        		public boolean match(Pilot pilot){
        			return pilot.getName().equals("Michael Schumacher");
        		}
        	});
        listResult(result);
    }

    public static void retrievePilotByExactPoints(ObjectContainer db) {
        List result=db.query(
            	new Predicate() {
            		public boolean match(Pilot pilot){
            			return pilot.getPoints() == 100;
            		}
            	});
        listResult(result);
    }

    public static void updatePilot(ObjectContainer db) {
        List result=db.query(
            new Predicate() {
            	public boolean match(Pilot pilot){
            		return pilot.getName().equals("Michael Schumacher");
            	}
            });
        Pilot found=(Pilot)result.get(0);
        found.addPoints(11);
        db.set(found);
        System.out.println("Added 11 points for "+found);
        retrieveAllPilots(db);
    }
    
    public static void deleteFirstPilotByName(ObjectContainer db) {
        List result=db.query(
                new Predicate() {
                	public boolean match(Pilot pilot){
                		return pilot.getName().equals("Michael Schumacher");
                	}
                });
        Pilot found=(Pilot)result.get(0);
        db.delete(found);
        System.out.println("Deleted "+found);
        retrieveAllPilots(db);
    }

    public static void deleteSecondPilotByName(ObjectContainer db) {
        List result=db.query(
                new Predicate() {
                	public boolean match(Pilot pilot){
                		return pilot.getName().equals("Rubens Barrichello");
                	}
                });
        Pilot found=(Pilot)result.get(0);
        db.delete(found);
        System.out.println("Deleted "+found);
        retrieveAllPilots(db);
    }
}
