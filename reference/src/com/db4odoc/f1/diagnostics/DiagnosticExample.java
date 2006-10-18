package com.db4odoc.f1.diagnostics;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.db4o.diagnostic.*;


public class DiagnosticExample{
	public final static String YAPFILENAME="formula1.yap";
    public static void testEmpty() {
    	Db4o.configure().diagnostic().addListener(new DiagnosticToConsole());
        new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
        	setEmptyObject(db);
        }
        finally {
            db.close();
            Db4o.configure().diagnostic().removeAllListeners();
        }
    }
    // end testEmpty
    
    private static void setEmptyObject(ObjectContainer db){
    	Empty empty = new Empty();
        db.set(empty);
    }
    // end setEmptyObject
    	
    public static void testArbitrary() {
    	Db4o.configure().diagnostic().addListener(new DiagnosticToConsole());
    	new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
        	Pilot pilot = new Pilot("Rubens Barrichello",99);
        	db.set(pilot);
        	queryPilot(db);
        }
        finally {
            db.close();
            Db4o.configure().diagnostic().removeAllListeners();
        }
    }
    // end testArbitrary
	
    private static void queryPilot(ObjectContainer db){
    	int[]  i = new int[]{19,100};
    	ObjectSet result = db.query(new ArbitraryQuery(i));
    	listResult(result);
    }
    // end queryPilot
    
    public static void testIndexDiagnostics() {
    	Db4o.configure().diagnostic().addListener(new IndexDiagListener());
    	Db4o.configure().updateDepth(3);
        new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
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
            Db4o.configure().diagnostic().removeAllListeners();
        }
    }
    // end testIndexDiagnostics
     
    public static void testTranslatorDiagnostics() {
    	storeTranslatedCars();
    	retrieveTranslatedCars();
    	retrieveTranslatedCarsNQ();
    	retrieveTranslatedCarsNQUnopt();
    	retrieveTranslatedCarsSODAEv();
    }
    // end testTranslatorDiagnostics
    
    public static void storeTranslatedCars() {
    	Db4o.configure().exceptionsOnNotStorable(true);
    	Db4o.configure().objectClass(Car.class).translate(new CarTranslator());
    	Db4o.configure().objectClass(Car.class).callConstructor(true);
    	new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Car car1 = new Car("BMW");
			System.out.println("ORIGINAL: " + car1);
			db.set(car1);
			Car car2 = new Car("Ferrari");
			System.out.println("ORIGINAL: " + car2);
			db.set(car2);
		} catch (Exception exc) {
			System.out.println(exc.toString());
			return;
		} finally {
			db.close();
		}
	}
    // end storeTranslatedCars

    public static void retrieveTranslatedCars() {
    	Db4o.configure().diagnostic().addListener(new TranslatorDiagListener());
    	Db4o.configure().exceptionsOnNotStorable(true);
    	Db4o.configure().objectClass(Car.class).translate(new CarTranslator());
    	Db4o.configure().objectClass(Car.class).callConstructor(true);
    	ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet  result = db.query(Car.class);
			listResult(result);
		} finally {
			db.close();
			Db4o.configure().diagnostic().removeAllListeners();
		}
	}
    // end retrieveTranslatedCars

    public static void retrieveTranslatedCarsNQ() {
    	Db4o.configure().diagnostic().addListener(new TranslatorDiagListener());
    	Db4o.configure().exceptionsOnNotStorable(true);
    	Db4o.configure().objectClass(Car.class).translate(new CarTranslator());
    	Db4o.configure().objectClass(Car.class).callConstructor(true);
    	ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet  result = db.query(new NewCarModel());
			listResult(result);
		} finally {
			db.close();
			Db4o.configure().diagnostic().removeAllListeners();
		}
	}
    // end retrieveTranslatedCarsNQ
    
    public static void retrieveTranslatedCarsNQUnopt() {
    	Db4o.configure().optimizeNativeQueries(false);
    	Db4o.configure().diagnostic().addListener(new TranslatorDiagListener());
    	Db4o.configure().exceptionsOnNotStorable(true);
    	Db4o.configure().objectClass(Car.class).translate(new CarTranslator());
    	Db4o.configure().objectClass(Car.class).callConstructor(true);
    	ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet  result = db.query(new NewCarModel());
			listResult(result);
		} finally {
			Db4o.configure().optimizeNativeQueries(true);
			db.close();
			Db4o.configure().diagnostic().removeAllListeners();
		}
	}
    // end retrieveTranslatedCarsNQUnopt

    public static void retrieveTranslatedCarsSODAEv() {
    	Db4o.configure().diagnostic().addListener(new TranslatorDiagListener());
    	Db4o.configure().exceptionsOnNotStorable(true);
    	Db4o.configure().objectClass(Car.class).translate(new CarTranslator());
    	Db4o.configure().objectClass(Car.class).callConstructor(true);
    	ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Query query = db.query();
			query.constrain(Car.class);
			query.constrain(new CarEvaluation());
			ObjectSet  result = query.execute();
			listResult(result);
		} finally {
			db.close();
			Db4o.configure().diagnostic().removeAllListeners();
			Db4o.configure().objectClass(Car.class).translate(null);
		}
	}
    // end retrieveTranslatedCarsSODAEv
    
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
