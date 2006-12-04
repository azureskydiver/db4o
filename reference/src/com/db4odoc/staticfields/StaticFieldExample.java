/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.staticfields;

import java.awt.Color;
import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;


public class StaticFieldExample {
	public final static String YAPFILENAME="formula1.yap";

	public static void main(String[] args) {
        setPilotsSimple();
        checkPilots();
        checkDatabaseFileSize();
        //
        setPilotsStatic();
        checkPilots();
        checkDatabaseFileSize();
        updatePilots();
        updatePilotCategories();
        checkPilots();
        deleteTest();
	}
	// end main
	
	public static void setCar(){
		new File(YAPFILENAME).delete();
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
			Car car = new Car();
			car.color = Color.GREEN;
			db.set(car);
		} finally {
			db.close();
		}
	}
	// end setCar
	
	public static void setPilotsSimple(){
		System.out.println("In the default setting, static constants are not continously stored and updated.");
		new File(YAPFILENAME).delete();
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
			db.set(new Pilot("Michael Schumacher",PilotCategories.WINNER));
			db.set(new Pilot("Rubens Barrichello",PilotCategories.TALENTED));
		} finally {
			db.close();
		}
	}
	// end setPilotsSimple
	
	public static void setPilotsStatic(){
		System.out.println("The feature can be turned on for individual classes.");
		Db4o.configure().objectClass("com.db4odoc.f1.staticfields.PilotCategories").persistStaticFieldValues();
		new File(YAPFILENAME).delete();
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
			db.set(new Pilot("Michael Schumacher",PilotCategories.WINNER));
			db.set(new Pilot("Rubens Barrichello",PilotCategories.TALENTED));
		} finally {
			db.close();
		}
	}
	// end setPilotsStatic
	
	public static void checkPilots(){
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
	        ObjectSet result = db.query(Pilot.class);
	        for(int x = 0; x < result.size(); x++){
	        	Pilot pilot = (Pilot )result.get(x);
	        	if (pilot.getCategory()  == PilotCategories.WINNER){
	        		System.out.println("Winner pilot: " + pilot);
	        	} else if (pilot.getCategory() == PilotCategories.TALENTED){
	        		System.out.println("Talented pilot: " + pilot);
	        	}  else {
	        		System.out.println("Uncategorized pilot: " + pilot);
	        	}
	        }
		} finally {
			db.close();
		}
    }
	// end checkPilots
	
	public static void updatePilots(){
		System.out.println("Updating PilotCategory in pilot reference:");
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
	        ObjectSet result = db.query(Pilot.class);
	        for(int x = 0; x < result.size(); x++){
	        	Pilot pilot = (Pilot )result.get(x);
	        	if (pilot.getCategory()  == PilotCategories.WINNER){
	        		System.out.println("Winner pilot: " + pilot);
	        		PilotCategories pc = pilot.getCategory();
	        		pc.testChange("WINNER2006");
	        		db.set(pilot);
	        	}
	        }
		} finally {
			db.close();
		}
		printCategories();
    }
	// end updatePilots
	
	public static void updatePilotCategories(){
		System.out.println("Updating PilotCategories explicitly:");
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
	        ObjectSet result = db.query(PilotCategories.class);
	        for(int x = 0; x < result.size(); x++){
	        	PilotCategories pc = (PilotCategories)result.get(x);
	        	if (pc == PilotCategories.WINNER){
	        		pc.testChange("WINNER2006");
	        		db.set(pc);
	        	}
	        }
		} finally {
			db.close();
		}
		printCategories();
		System.out.println("Change the value back:");
		db=Db4o.openFile(YAPFILENAME);
		try {
	        ObjectSet result = db.query(PilotCategories.class);
	        for(int x = 0; x < result.size(); x++){
	        	PilotCategories pc = (PilotCategories)result.get(x);
	        	if (pc == PilotCategories.WINNER){
	        		pc.testChange("WINNER");
	        		db.set(pc);
	        	}
	        }
		} finally {
			db.close();
		}
		printCategories();
    }
	// end updatePilotCategories
	
	public static void deleteTest(){
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		db.ext().configure().objectClass(Pilot.class).cascadeOnDelete(true);
		try {
			System.out.println("Deleting Pilots :");
	        ObjectSet result = db.query(Pilot.class);
	        for(int x = 0; x < result.size(); x++){
	        	Pilot pilot = (Pilot )result.get(x);
	        	db.delete(pilot);
	        }
	        printCategories();
	        System.out.println("Deleting PilotCategories :");
	        result = db.query(PilotCategories.class);
	        for(int x = 0; x < result.size(); x++){
	        	db.delete(result.get(x));
	        }
	        printCategories();
		} finally {
			db.close();
		}
    }
	// end deleteTest
	
	public static void printCategories(){
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet  result = db.query(PilotCategories.class);
			System.out.println("Stored categories: " + result.size());
	        for(int x = 0; x < result.size(); x++){
	        	PilotCategories pc = (PilotCategories)result.get(x);
	        	System.out.println("Category: "+pc);
	        }
		} finally {
			db.close();
		}
	}
	// end printCategories
	
	public static void deletePilotCategories(){
		printCategories();
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet  result = db.query(PilotCategories.class);
	        for(int x = 0; x < result.size(); x++){
	        	PilotCategories pc = (PilotCategories)result.get(x);
	        	db.delete(pc);
	        }
		} finally {
			db.close();
		}
		printCategories();
    }
	// end deletePilotCategories
    
    private static void checkDatabaseFileSize(){
        System.out.println("Database file size: " + new File(YAPFILENAME).length() + "\n");
    }
    // end checkDatabaseFileSize
}
