/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.debugging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

public class DebugExample {
	public final static String YAPFILENAME="formula1.yap";

	public static void main(String[] args) {
		setCars();
		try {
			setCarsWithFileOutput();
		} catch (Exception ex) {
			//
		}
	}
	// end main

	public static void setCars()
	{
		 // Set the debug message levet to the maximum
		Db4o.configure().messageLevel(3);
		
		 // Do some db4o operations
		new File(YAPFILENAME).delete();
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
			Car car1 = new Car("BMW");
			db.set(car1);
			Car car2 = new Car("Ferrari");
			db.set(car2);
			db.deactivate(car1,2);
			Query query = db.query();
			query.constrain(Car.class);
			ObjectSet results = query.execute();
			listResult(results);
		} finally {
			db.close();
		}
		Db4o.configure().messageLevel(0);
	}
	// end setCars
	
	public static void setCarsWithFileOutput() throws FileNotFoundException
	{
		// Create StreamWriter for a file
		FileOutputStream fos = new FileOutputStream("Debug.txt");
		PrintStream debugWriter = new PrintStream(fos);
        
        // Redirect debug output to the specified writer
        Db4o.configure().setOut(debugWriter);
        
        // Set the debug message levet to the maximum
		Db4o.configure().messageLevel(3);
		
		 // Do some db4o operations
		 new File(YAPFILENAME).delete();
		 ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
			Car car1 = new Car("BMW");
			db.set(car1);
			Car car2 = new Car("Ferrari");
			db.set(car2);
			db.deactivate(car1,2);
			Query query = db.query();
			query.constrain(Car.class);
			ObjectSet results = query.execute();
			listResult(results);
		} finally {
			db.close();
			debugWriter.close();
		}
		Db4o.configure().messageLevel(0);
	}
	// end setCarsWithFileOutput
	
	public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
