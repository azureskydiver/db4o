/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.cachedIO;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.io.CachedIoAdapter;
import com.db4o.io.RandomAccessFileAdapter;

public class CachedIOExample  {
	private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void main(String[] args) {
		Configuration configuration = Db4o.newConfiguration();
		setObjects(configuration);
		getObjects(configuration);
		configuration = configureCache();
		setObjects(configuration);
		getObjects(configuration);
	}
	// end main
	
	private static void setObjects(Configuration configuration){
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			long t1 = System.currentTimeMillis();
			for (int i = 0; i< 50000; i++){
				Pilot pilot = new Pilot("Pilot #"+i);
				container.store(pilot);
			}
			long t2 = System.currentTimeMillis();
			long  timeElapsed = t2 - t1;
			System.out.println("Time elapsed for setting objects ="+ timeElapsed + " ms");
			t1 = System.currentTimeMillis();
			container.commit();
			t2 = System.currentTimeMillis();
			timeElapsed = t2 - t1;
			System.out.println("Time elapsed for commit =" + timeElapsed + " ms");
		} finally {
			container.close();
		}
	}
	// end setObjects

	private static Configuration configureCache(){
		System.out.println("Setting up cached io adapter");
		Configuration configuration = Db4o.newConfiguration();
		//	new cached IO adapter with 256 pages 1024 bytes each
		CachedIoAdapter adapter = new CachedIoAdapter(new RandomAccessFileAdapter(), 1024, 256);
		configuration.io(adapter);
		return configuration;
	}
	// end configureCache
	
	private static Configuration configureRandomAccessAdapter(){
		System.out.println("Setting up random access io adapter");
		Configuration configuration = Db4o.newConfiguration();
		configuration.io(new RandomAccessFileAdapter());
		return configuration;
	}
	// end configureRandomAccessAdapter

	private static void getObjects(Configuration configuration){
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			long t1 = System.currentTimeMillis();
			ObjectSet result=container.queryByExample(null);
			long t2 = System.currentTimeMillis();
			long  timeElapsed = t2 - t1;
			System.out.println("Time elapsed for the query ="+ timeElapsed + " ms");
			System.out.println("Objects in the database: " + result.size());
		} finally {
			container.close();
		}
	}
	// end getObjects
}
