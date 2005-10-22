package com.db4o.f1.chapter1;


import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.f1.Util;


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
        List<Pilot> result = db.query<Pilot>(
        	new Predicate <Pilot> {
        		public boolean match(Pilot pilot){return true;}
        	});
        listResult(result);
    }
    
    public static void retrievePilotByName(ObjectContainer db) {
        List<Pilot> result=db.query<Pilot>(
        	new Predicate <Pilot> {
        		public boolean match(Pilot pilot){
        			return pilot.getName() == "Michael Schumacher";
        		}
        	});
        listResult(result);
    }

    public static void retrievePilotByExactPoints(ObjectContainer db) {
        List<Pilot> result=db.query<Pilot>(
            	new Predicate <Pilot> {
            		public boolean match(Pilot pilot){
            			return pilot.getPoints() == 100;
            		}
            	});
        listResult(result);
    }

    public static void updatePilot(ObjectContainer db) {
        List<Pilot> result=db.query<Pilot>(
            new Predicate <Pilot> {
            	public boolean match(Pilot pilot){
            		return pilot.getName() == "Michael Schumacher";
            	}
            });
        Pilot found=result[0];
        found.addPoints(11);
        db.set(found);
        System.out.println("Added 11 points for "+found);
        retrieveAllPilotsNQ(db);
    }
    
    public static void deleteFirstPilotByName(ObjectContainer db) {
        List<Pilot> result=db.query<Pilot>(
                new Predicate <Pilot> {
                	public boolean match(Pilot pilot){
                		return pilot.getName() == "Michael Schumacher";
                	}
                });
        Pilot found=result[0];
        db.delete(found);
        System.out.println("Deleted "+found);
        retrieveAllPilots(db);
    }

    public static void deleteSecondPilotByName(ObjectContainer db) {
        List<Pilot> result=db.query<Pilot>(
                new Predicate <Pilot> {
                	public boolean match(Pilot pilot){
                		return pilot.getName() == "Rubens Barrichello";
                	}
                });
        Pilot found=result[0];
        db.delete(found);
        System.out.println("Deleted "+found);
        retrieveAllPilots(db);
    }
}
