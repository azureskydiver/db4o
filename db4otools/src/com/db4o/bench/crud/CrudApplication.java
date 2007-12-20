/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench.crud;

import java.io.*;

import com.db4o.*;
import com.db4o.bench.*;
import com.db4o.bench.logging.*;
import com.db4o.config.*;
import com.db4o.io.*;

/**
 * Very simple CRUD (Create, Read, Update, Delete) application to 
 * produce log files as an input for I/O-benchmarking.
 */
public class CrudApplication {
	
	
	private static final int ITEM_COUNT = 10000;
	private static final String DATABASE_FILE = "simplecrud.db4o";
	
	
	public static void main(String[] args) {
		new CrudApplication().run(ITEM_COUNT);
	}

	public void run(int itemCount) {
		Configuration config = prepare(itemCount);
		create(itemCount, config);
		read(config);
		update(config);
		delete(config);
		deleteDbFile();
	}

	private void create(int itemCount, Configuration config) {
		ObjectContainer oc = open(config);
		for (int i = 0; i < itemCount; i++) {
			oc.set(Item.newItem(i));
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
		ObjectSet objectSet = allItems(oc);
		while(objectSet.hasNext()){
			Item item = (Item) objectSet.next();
		}
		oc.close();
	}
	
	private void update(Configuration config) {
		ObjectContainer oc = open(config);
		ObjectSet objectSet = allItems(oc);
		while(objectSet.hasNext()){
			Item item = (Item) objectSet.next();
			item.change();
			oc.set(item);
		}
		oc.close();
	}

	private void delete(Configuration config) {
		ObjectContainer oc = open(config);
		ObjectSet objectSet = allItems(oc);
		while(objectSet.hasNext()){
			oc.delete(objectSet.next());
			// adding commit results in more syncs in the log, which is necessary for
			// meaningful statistics!
			oc.commit();	 
		}
		oc.close();
	}

	private Configuration prepare(int itemCount) {
		deleteDbFile();
		RandomAccessFileAdapter rafAdapter = new RandomAccessFileAdapter();
		IoAdapter ioAdapter = new LoggingIoAdapter(rafAdapter, logFileName(itemCount));
		Configuration config = Db4o.cloneConfiguration();
		config.io(ioAdapter);
		return config;
	}

	private void deleteDbFile() {
		new File(DATABASE_FILE).delete();
	}

	private ObjectSet allItems(ObjectContainer oc) {
		return oc.query(Item.class);
	}

	private ObjectContainer open(Configuration config) {
		return Db4o.openFile(config, DATABASE_FILE);
	}

	public static String logFileName(int itemCount) {
		return "simplecrud_" + itemCount + ".log";
	}
	

	
	
	
	

}
