/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.cachedIO;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.io.CachedIoAdapter;
import com.db4o.io.RandomAccessFileAdapter;
import com.db4odoc.ios.LoggingAdapter;

public class CachedIOExample  {
	public final static String YAPFILENAME="formula1.yap";
	
	public static void main(String[] args) {
		setObjects();
		getObjects();
		configureCache();
		setObjects();
		getObjects();
	}
	// end main
	
	public static void setObjects(){
		new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			long t1 = System.currentTimeMillis();
			for (int i = 0; i< 50000; i++){
				Pilot pilot = new Pilot("Pilot #"+i);
				db.set(pilot);
			}
			long t2 = System.currentTimeMillis();
			long  timeElapsed = t2 - t1;
			System.out.println("Time elapsed for setting objects ="+ timeElapsed + " ms");
			t1 = System.currentTimeMillis();
			db.commit();
			t2 = System.currentTimeMillis();
			timeElapsed = t2 - t1;
			System.out.println("Time elapsed for commit =" + timeElapsed + " ms");
		} finally {
			db.close();
		}
	}
	// end setObjects

	public static void configureCache(){
		System.out.println("Setting up cached io adapter");
		//	new cached IO adapter with 256 pages 1024 bytes each
		CachedIoAdapter adapter = new CachedIoAdapter(new RandomAccessFileAdapter(), 1024, 256);
		Db4o.configure().io(adapter);
	}
	// end configureCache
	
	public static void configureRandomAccessAdapter(){
		System.out.println("Setting up random access io adapter");
		Db4o.configure().io(new RandomAccessFileAdapter());
	}
	// end configureRandomAccessAdapter

	public static void getObjects(){
		
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			long t1 = System.currentTimeMillis();
			ObjectSet result=db.get(null);
			long t2 = System.currentTimeMillis();
			long  timeElapsed = t2 - t1;
			System.out.println("Time elapsed for the query ="+ timeElapsed + " ms");
			System.out.println("Objects in the database: " + result.size());
		} finally {
			db.close();
		}
	}
	// end getObjects
	
}
