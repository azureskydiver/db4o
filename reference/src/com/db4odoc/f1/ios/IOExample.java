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
import com.db4odoc.f1.Util;
import com.db4odoc.f1.uuids.Pilot;

public class IOExample extends Util {
	
	public static void main(String[] args) {
		setObjects();
		getObjectsInMem();
		getObjects();
		testLoggingAdapter();
	}
	
	public static void setObjects(){
		new File(Util.YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		try {
			Pilot pilot = new Pilot("Rubens Barrichello");
			db.set(pilot);
		} finally {
			db.close();
		}
	}
	
	public static void getObjectsInMem(){
		System.out.println("Setting up in-memory database");
		MemoryIoAdapter adapter = new MemoryIoAdapter();
		try {
			RandomAccessFile raf = new RandomAccessFile(Util.YAPFILENAME,"r"); 
			adapter.growBy(100);
			
			int len = (int)raf.length();
			byte[] b = new byte[len];
			raf.read(b,0,len);
			adapter.put(Util.YAPFILENAME, b);
			raf.close();
		} catch (Exception ex){
			System.out.println("Exception: " + ex.getMessage());
		}
		
		Db4o.configure().io(adapter);
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
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
		byte[] dbstream = adapter.get(Util.YAPFILENAME);
		try {
			RandomAccessFile file = new RandomAccessFile(Util.YAPFILENAME,"rw");
			file.write(dbstream);
			file.close();
		} catch (IOException ioex) {
			System.out.println("Exception: " + ioex.getMessage());
		} 
		Db4o.configure().io(new RandomAccessFileAdapter());
	}
	
	public static void getObjects(){
		Db4o.configure().io(new RandomAccessFileAdapter());
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		try {
			 ObjectSet result=db.get(Pilot.class);
			 System.out.println("Read stored results through disc file");
		     listResult(result);
		} finally {
			db.close();
		}
	}

	public static void testLoggingAdapter(){
		Db4o.configure().io(new LoggingAdapter());
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		try {
		     Pilot pilot = new Pilot("Michael Schumacher");
		     db.set(pilot);
		     System.out.println("New pilot added");
		} finally {
			db.close();
		}
	
		db = Db4o.openFile(Util.YAPFILENAME);
		try {
			 ObjectSet result=db.get(Pilot.class);
			 listResult(result);
		} finally {
			db.close();
		}
		Db4o.configure().io(new RandomAccessFileAdapter());
	}

}
