/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.diagnostics;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.diagnostic.DiagnosticToConsole;
import com.db4o.query.Query;


public class DiagnosticExample{
	private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void main(String[] args){
		testEmpty();
        testArbitrary();  
        testIndexDiagnostics();
        testTranslatorDiagnostics();
	}
	// end main
	
	private static void testEmpty() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.diagnostic().addListener(new DiagnosticToConsole());
        new File(DB4O_FILE_NAME).delete();
        ObjectContainer container=Db4o.openFile(configuration, DB4O_FILE_NAME);
        try {
        	setEmptyObject(container);
        }
        finally {
            container.close();
        }
    }
    // end testEmpty
    
    private static void setEmptyObject(ObjectContainer container){
    	Empty empty = new Empty();
        container.store(empty);
    }
    // end setEmptyObject
    	
    private static void testArbitrary() {
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.diagnostic().addListener(new DiagnosticToConsole());
    	new File(DB4O_FILE_NAME).delete();
        ObjectContainer container=Db4o.openFile(configuration, DB4O_FILE_NAME);
        try {
        	Pilot pilot = new Pilot("Rubens Barrichello",99);
        	container.store(pilot);
        	queryPilot(container);
        }
        finally {
            container.close();
        }
    }
    // end testArbitrary
	
    private static void queryPilot(ObjectContainer container){
    	int[]  i = new int[]{19,100};
    	ObjectSet result = container.query(new ArbitraryQuery(i));
    	listResult(result);
    }
    // end queryPilot
    
    private static void testIndexDiagnostics() {
    	new File(DB4O_FILE_NAME).delete();
    	
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.diagnostic().addListener(new IndexDiagListener());
    	//configuration.objectClass(Pilot.class).objectField("name").indexed(true);
    	//configuration.updateDepth(3);
        
        ObjectContainer container=Db4o.openFile(configuration, DB4O_FILE_NAME);
        try {
        	Pilot pilot1 = new Pilot("Rubens Barrichello",99);
        	container.store(pilot1);
        	Pilot pilot2 = new Pilot("Michael Schumacher",100);
        	container.store(pilot2);
        	queryPilot(container);
        	setEmptyObject(container);
        	Query query = container.query();
        	query.constrain(Pilot.class);
			query.descend("points").constrain(new Integer(99));
			ObjectSet  result = query.execute();
			listResult(result);
        }
        finally {
            container.close();
        }
    }
    // end testIndexDiagnostics
     
    private static void testTranslatorDiagnostics() {
    	storeTranslatedCars();
    	retrieveTranslatedCars();
    	retrieveTranslatedCarsNQ();
    	retrieveTranslatedCarsNQUnopt();
    	retrieveTranslatedCarsSODAEv();
    }
    // end testTranslatorDiagnostics
    
    private static void storeTranslatedCars() {
    	new File(DB4O_FILE_NAME).delete();
    	
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.exceptionsOnNotStorable(true);
    	configuration.objectClass(Car.class).translate(new CarTranslator());
    	configuration.objectClass(Car.class).callConstructor(true);
    	
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Car car1 = new Car("BMW");
			System.out.println("ORIGINAL: " + car1);
			container.store(car1);
			Car car2 = new Car("Ferrari");
			System.out.println("ORIGINAL: " + car2);
			container.store(car2);
		} catch (Exception exc) {
			System.out.println(exc.toString());
			return;
		} finally {
			container.close();
		}
	}
    // end storeTranslatedCars

    private static void retrieveTranslatedCars() {
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.diagnostic().addListener(new TranslatorDiagListener());
    	configuration.exceptionsOnNotStorable(true);
    	configuration.objectClass(Car.class).translate(new CarTranslator());
    	configuration.objectClass(Car.class).callConstructor(true);
    	ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			ObjectSet  result = container.query(Car.class);
			listResult(result);
		} finally {
			container.close();
		}
	}
    // end retrieveTranslatedCars

    private static void retrieveTranslatedCarsNQ() {
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.diagnostic().addListener(new TranslatorDiagListener());
    	configuration.exceptionsOnNotStorable(true);
    	configuration.objectClass(Car.class).translate(new CarTranslator());
    	configuration.objectClass(Car.class).callConstructor(true);
    	ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			ObjectSet  result = container.query(new NewCarModel());
			listResult(result);
		} finally {
			container.close();
		}
	}
    // end retrieveTranslatedCarsNQ
    
    private static void retrieveTranslatedCarsNQUnopt() {
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.optimizeNativeQueries(false);
    	configuration.diagnostic().addListener(new TranslatorDiagListener());
    	configuration.exceptionsOnNotStorable(true);
    	configuration.objectClass(Car.class).translate(new CarTranslator());
    	configuration.objectClass(Car.class).callConstructor(true);
    	ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			ObjectSet  result = container.query(new NewCarModel());
			listResult(result);
		} finally {
			container.close();
		}
	}
    // end retrieveTranslatedCarsNQUnopt

    private static void retrieveTranslatedCarsSODAEv() {
    	Configuration configuration = Db4o.newConfiguration();
    	configuration.diagnostic().addListener(new TranslatorDiagListener());
    	configuration.exceptionsOnNotStorable(true);
    	configuration.objectClass(Car.class).translate(new CarTranslator());
    	configuration.objectClass(Car.class).callConstructor(true);
    	ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Query query = container.query();
			query.constrain(Car.class);
			query.constrain(new CarEvaluation());
			ObjectSet  result = query.execute();
			listResult(result);
		} finally {
			container.close();
		}
	}
    // end retrieveTranslatedCarsSODAEv
    
    private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
