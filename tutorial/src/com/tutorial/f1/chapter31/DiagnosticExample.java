package com.tutorial.f1.chapter31;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.f1.Util;
import com.db4o.query.Query;
import com.db4o.diagnostic.*;


public class DiagnosticExample extends Util {
    public static void testEmpty() {
    	Db4o.configure().diagnostic().addListener(new DiagnosticToConsole());
        new File(Util.YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
        	setEmptyObject(db);
        }
        finally {
            db.close();
        }
    }
    
    private static void setEmptyObject(ObjectContainer db){
    	Empty empty = new Empty();
        db.set(empty);
    }
    	
    public static void testArbitrary() {
    	Db4o.configure().diagnostic().addListener(new DiagnosticToConsole());
    	new File(Util.YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
        	Pilot pilot = new Pilot("Rubens Barrichello",99);
        	db.set(pilot);
        	queryPilot(db);
        }
        finally {
            db.close();
        }
    }
	
    private static void queryPilot(ObjectContainer db){
    	int[]  i = new int[]{19,100};
    	ObjectSet result = db.query(new ArbitraryQuery(i));
    	listResult(result);
    }
    public static void testIndexDiagnostics() {
    	Db4o.configure().diagnostic().removeAllListeners();
    	Db4o.configure().diagnostic().addListener(new IndexDiagListener());
    	Db4o.configure().updateDepth(3);
        new File(Util.YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
        try {
        	Pilot pilot1 = new Pilot("Rubens Barrichello",99);
        	db.set(pilot1);
        	Pilot pilot2 = new Pilot("Michael Schumacher",100);
        	db.set(pilot2);
        	queryPilot(db);
        	setEmptyObject(db);
        	Query query = db.query();
        	query.constrain(Pilot.class);
			query.descend("points").constrain(new Integer(99));
			ObjectSet  result = query.execute();
			listResult(result);
        }
        finally {
            db.close();
        }
    }
       
}
