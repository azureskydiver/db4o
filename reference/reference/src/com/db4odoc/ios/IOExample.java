/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.ios;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.io.MemoryIoAdapter;
import com.db4o.io.RandomAccessFileAdapter;

public class IOExample  {
	private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void main(String[] args) {
		setObjects();
//		getObjectsInMem();
//		getObjects();
		testLoggingAdapter();
	}
	// end main
	
	private static void setObjects(){
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Pilot pilot = new Pilot("Rubens Barrichello");
			container.store(pilot);
		} finally {
			container.close();
		}
	}
	// end setObjects
	
	private static void getObjectsInMem(){
		System.out.println("Setting up in-memory database");
		MemoryIoAdapter adapter = new MemoryIoAdapter();
		try {
			RandomAccessFile raf = new RandomAccessFile(DB4O_FILE_NAME,"r"); 
			adapter.growBy(100);
			
			int len = (int)raf.length();
			byte[] b = new byte[len];
			raf.read(b,0,len);
			adapter.put(DB4O_FILE_NAME, b);
			raf.close();
		} catch (Exception ex){
			System.out.println("Exception: " + ex.getMessage());
		}
		
		Configuration configuration = Db4o.newConfiguration();
		configuration.io(adapter);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			 ObjectSet result=container.queryByExample(Pilot.class);
			 System.out.println("Read stored results through memory file");
		     listResult(result);
		     Pilot pilotNew = new Pilot("Michael Schumacher");
		     container.store(pilotNew);
		     System.out.println("New pilot added");
		} finally {
			container.close();
		}
		System.out.println("Writing the database back to disc");
		byte[] dbstream = adapter.get(DB4O_FILE_NAME);
		try {
			RandomAccessFile file = new RandomAccessFile(DB4O_FILE_NAME,"rw");
			file.write(dbstream);
			file.close();
		} catch (IOException ioex) {
			System.out.println("Exception: " + ioex.getMessage());
		} 
	}
	// end getObjectsInMem
	
	private static void getObjects(){
		Configuration configuration = Db4o.newConfiguration();
		configuration.io(new RandomAccessFileAdapter());
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			 ObjectSet result=container.queryByExample(Pilot.class);
			 System.out.println("Read stored results through disc file");
		     listResult(result);
		} finally {
			container.close();
		}
	}
	// end getObjects

	private static void testLoggingAdapter(){
		Configuration configuration = Db4o.newConfiguration();
		configuration.io(new LoggingAdapter());
		ObjectServer server = Db4o.openServer(configuration, DB4O_FILE_NAME, 0xdb40);
		server.grantAccess("y", "y");
		ObjectContainer container = server.openClient();
		//ObjectContainer container = Db4o.openClient(configuration, "localhost", 0xdb40,"y", "y");
		try {
		     Pilot pilot = new Pilot("Michael Schumacher");
		     container.store(pilot);
		     System.out.println("New pilot added");
		} finally {
			
			container.close();
			server.close();
		}
	
		container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			 ObjectSet result=container.queryByExample(Pilot.class);
			 listResult(result);
		} finally {
			container.close();
		}
	}
	// end testLoggingAdapter

	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
