/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.enums;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;


public class EnumExample {
	private final static String DB4O_FILE_NAME="reference.db4o";
	public static void main(String[] args) {
        setPilots();
        checkPilots();
    
        deletePilots();
        checkPilots();
        deleteQualification();
        updateQualification();
  }
	// end main
	
	
	
	private static void setPilots(){
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		try {
			container.store(new Pilot("Michael Schumacher",Qualification.WINNER));
			container.store(new Pilot("Rubens Barrichello",Qualification.PROFESSIONAL));
		} finally {
			container.close();
		}
	}
	// end setPilots
	
	private static void checkPilots(){
		ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		try {
	        ObjectSet result = container.query(Pilot.class);
	        System.out.println("Saved pilots: " + result.size());
	        for(int x = 0; x < result.size(); x++){
	        	Pilot pilot = (Pilot )result.get(x);
	        	if (pilot.getQualification() == Qualification.WINNER){
	        		System.out.println("Winner pilot: " + pilot);
	        	} else if (pilot.getQualification() == Qualification.PROFESSIONAL){
	        		System.out.println("Professional pilot: " + pilot);
	        	}  else {
	        		System.out.println("Uncategorized pilot: " + pilot);
	        	}
	        }
		} finally {
			container.close();
		}
    }
	// end checkPilots
	
	private static void updateQualification(){
		System.out.println("Updating WINNER qualification constant");
		ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		try {
			Query query = container.query();
			query.constrain(Qualification.class);
			query.descend("qualification").constrain("WINNER");
	        ObjectSet result = query.execute();
	        for(int x = 0; x < result.size(); x++){
	        	Qualification qualification = (Qualification)result.get(x);
	        	qualification.testChange("WINNER2006");
	        	container.store(qualification);
	        }
		} finally {
			container.close();
		}
		printQualification();
    }
	// end updateQualification
	
	private static void deletePilots(){
		System.out.println("Qualification enum before delete Pilots");
		printQualification();
		Configuration configuration = Db4o.newConfiguration();
		configuration .objectClass(Pilot.class).objectField("qualification").cascadeOnDelete(true);
		ObjectContainer container=Db4o.openFile(configuration, DB4O_FILE_NAME);

		try {
	        ObjectSet result = container.query(Pilot.class);
	        for(int x = 0; x < result.size(); x++){
	        	Pilot pilot = (Pilot )result.get(x);
	        	container.delete(pilot);
	        }
		} finally {
			container.close();
		}
		System.out.println("Qualification enum after delete Pilots");
		printQualification();
    }
	// end deletePilots
	
	private static void printQualification(){
		ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet  result = container.query(Qualification.class);
			System.out.println("results: " + result.size());
	        for(int x = 0; x < result.size(); x++){
	        	Qualification pq = (Qualification)result.get(x);
	        	System.out.println("Category: "+pq);
	        }
		} finally {
			container.close();
		}
	}
	// end printQualification
	
	private static void deleteQualification(){
		System.out.println("Explicit delete of Qualification enum");
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(Qualification.class).cascadeOnDelete(true);
		ObjectContainer container=Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			ObjectSet  result = container.query(Qualification.class);
	        for(int x = 0; x < result.size(); x++){
	        	Qualification pq = (Qualification)result.get(x);
	        	container.delete(pq);
	        }
		} finally {
			container.close();
		}
		printQualification();
    }
	// end deleteQualification
}
