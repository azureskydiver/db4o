/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.debugging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.diagnostic.Diagnostic;
import com.db4o.diagnostic.DiagnosticListener;
import com.db4o.query.Query;

public class DebugExample {
	private final static String DB4O_FILE_NAME="reference.db4o";

	public static void main(String[] args) {
		setCars();
		try {
			setCarsWithFileOutput();
		} catch (Exception ex) {
			//
		}
	}
	// end main

	private static void setCars()
	{
		 // Set the debug message level to the maximum
		Configuration configuration = Db4o.newConfiguration();
		configuration.messageLevel(3);
		configuration.diagnostic().addListener(new DiagnosticListener(){
			public void onDiagnostic(Diagnostic d) {
				    System.out.println(d.toString());
			    }
		});
		
		 // Do some db4o operations
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container=Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Car car1 = new Car("BMW");
			container.store(car1);
			Car car2 = new Car("Ferrari");
			container.store(car2);
			container.deactivate(car1,2);
		}finally {
			container.close();
		}
		container=Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Query query = container.query();
			query.constrain(Car.class);
			query.descend("model").constrain("BMW");
			ObjectSet results = query.execute();
			listResult(results);
		} finally {
			container.close();
		}
	}
	// end setCars
	
	private static void setCarsWithFileOutput() throws FileNotFoundException
	{
		// Create StreamWriter for a file
		FileOutputStream fos = new FileOutputStream("Debug.txt");
		PrintStream debugWriter = new PrintStream(fos);
        
        // Redirect debug output to the specified writer
		Configuration configuration = Db4o.newConfiguration();
		configuration.setOut(debugWriter);
        
        // Set the debug message level to the maximum
		configuration.messageLevel(3);
		
		 // Do some db4o operations
		 new File(DB4O_FILE_NAME).delete();
		 ObjectContainer container=Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Car car1 = new Car("BMW");
			container.store(car1);
			Car car2 = new Car("Ferrari");
			container.store(car2);
			container.deactivate(car1,2);
			Query query = container.query();
			query.constrain(Car.class);
			ObjectSet results = query.execute();
			listResult(results);
		} finally {
			container.close();
			debugWriter.close();
		}
	}
	// end setCarsWithFileOutput
	
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
