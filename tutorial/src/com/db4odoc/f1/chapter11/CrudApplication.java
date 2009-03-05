/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4odoc.f1.chapter11;

import java.io.*;

import com.db4o.*;
import com.db4o.bench.logging.*;
import com.db4o.config.*;
import com.db4o.io.*;

/**
 * Very simple CRUD (Create, Read, Update, Delete) application to 
 * produce log files as an input for I/O-benchmarking.
 */
public class CrudApplication {
	
	private static final String DATABASE_FILE = "simplecrud.db4o";
	
	public void run(final int itemCount) {
		deleteDbFile();
		
		final String logFileName = logFileName(itemCount);
		
		create(itemCount, newConfigWithLogging(logFileName));
		read(newConfigWithLogging(logFileName));
		update(newConfigWithLogging(logFileName));
		delete(newConfigWithLogging(logFileName));
		
		deleteDbFile();
	}

	private Configuration newConfigWithLogging(final String logFileName) {
	    RandomAccessFileAdapter rafAdapter = new RandomAccessFileAdapter();
		IoAdapter ioAdapter = new LoggingIoAdapter(rafAdapter, logFileName);
		Configuration config = Db4o.newConfiguration();
		config.io(ioAdapter);
		return config;
    }

	private void create(int itemCount, Configuration config) {
		ObjectContainer oc = open(config);
		for (int i = 0; i < itemCount; i++) {
			oc.store(Item.newItem(i));
			// preventing heap space problems by committing from time to time
			if(i % 100000 == 0) {
				oc.commit();
			}
		}
		oc.commit();
		oc.close();
	}
	
	private void read(Configuration config) {
		ObjectContainer oc = open(config);
		ObjectSet objectSet = oc.query(Item.class);
		while(objectSet.hasNext()){
			Item item = (Item) objectSet.next();
		}
		oc.close();
	}
	
	private void update(Configuration config) {
		ObjectContainer oc = open(config);
		ObjectSet objectSet = oc.query(Item.class);
		while(objectSet.hasNext()){
			Item item = (Item) objectSet.next();
			item.change();
			oc.store(item);
		}
		oc.close();
	}

	private void delete(Configuration config) {
		ObjectContainer oc = open(config);
		ObjectSet objectSet = oc.query(Item.class);;
		while(objectSet.hasNext()){
			oc.delete(objectSet.next());
			// adding commit results in more syncs in the log, 
			// which is necessary for meaningful statistics!
			oc.commit();	 
		}
		oc.close();
	}

	private void deleteDbFile() {
		new File(DATABASE_FILE).delete();
	}

	private ObjectContainer open(Configuration config) {
		return Db4o.openFile(config, DATABASE_FILE);
	}

	public static String logFileName(int itemCount) {
		return "simplecrud_" + itemCount + ".log";
	}
	
}
