/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.ios;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.io.MemoryIoAdapter;
import com.db4o.io.RandomAccessFileAdapter;

public class IOExample  {
	public final static String YAPFILENAME="formula1.yap";
	
	public static void main(String[] args) {
		setObjects();
		getObjectsInMem();
		getObjects();
		testLoggingAdapter();
	}
	// end main
	
	public static void setObjects(){
		new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Pilot pilot = new Pilot("Rubens Barrichello");
			db.set(pilot);
		} finally {
			db.close();
		}
	}
	// end setObjects
	
	public static void getObjectsInMem(){
		System.out.println("Setting up in-memory database");
		MemoryIoAdapter adapter = new MemoryIoAdapter();
		try {
			RandomAccessFile raf = new RandomAccessFile(YAPFILENAME,"r"); 
			adapter.growBy(100);
			
			int len = (int)raf.length();
			byte[] b = new byte[len];
			raf.read(b,0,len);
			adapter.put(YAPFILENAME, b);
			raf.close();
		} catch (Exception ex){
			System.out.println("Exception: " + ex.getMessage());
		}
		
		Db4o.configure().io(adapter);
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			 ObjectSet result=db.get(Pilot.class);
			 System.out.println("Read stored results through memory file");
		     listResult(result);
		     Pilot pilotNew = new Pilot("Michael Schumacher");
		     db.set(pilotNew);
		     System.out.println("New pilot added");
		} finally {
			db.close();
		}
		System.out.println("Writing the database back to disc");
		byte[] dbstream = adapter.get(YAPFILENAME);
		try {
			RandomAccessFile file = new RandomAccessFile(YAPFILENAME,"rw");
			file.write(dbstream);
			file.close();
		} catch (IOException ioex) {
			System.out.println("Exception: " + ioex.getMessage());
		} 
		Db4o.configure().io(new RandomAccessFileAdapter());
	}
	// end getObjectsInMem
	
	public static void getObjects(){
		Db4o.configure().io(new RandomAccessFileAdapter());
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			 ObjectSet result=db.get(Pilot.class);
			 System.out.println("Read stored results through disc file");
		     listResult(result);
		} finally {
			db.close();
		}
	}
	// end getObjects

	public static void testLoggingAdapter(){
		Db4o.configure().io(new LoggingAdapter());
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
		     Pilot pilot = new Pilot("Michael Schumacher");
		     db.set(pilot);
		     System.out.println("New pilot added");
		} finally {
			db.close();
		}
	
		db = Db4o.openFile(YAPFILENAME);
		try {
			 ObjectSet result=db.get(Pilot.class);
			 listResult(result);
		} finally {
			db.close();
		}
		Db4o.configure().io(new RandomAccessFileAdapter());
	}
	// end testLoggingAdapter

    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
