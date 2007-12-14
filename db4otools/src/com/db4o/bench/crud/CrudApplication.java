/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench.crud;

import java.io.*;

import com.db4o.*;
import com.db4o.bench.*;
import com.db4o.config.*;
import com.db4o.io.*;

/**
 * Very simple CRUD (Create, Read, Update, Delete) app to 
 * produce log files as an input for benchmarking IO.
 */
public class CrudApplication {
	
	
	private static final String DATABASE_FILE = "simplecrud.db4o";
	
	public static void main(String[] args) {
		new CrudApplication().run(10000);
	}

	public void run(int itemCount) {
		Configuration config = prepare(itemCount);
		create(itemCount, config);
		read(config);
		update(config);
		delete(config);
	}

	private void create(int itemCount, Configuration config) {
		ObjectContainer oc = open(config);
		for (int i = 0; i < itemCount; i++) {
			oc.set(Item.newItem(i));
		}
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
		}
		oc.close();
	}

	private Configuration prepare(int itemCount) {
		new File(DATABASE_FILE).delete();
		RandomAccessFileAdapter rafAdapter = new RandomAccessFileAdapter();
		IoAdapter ioAdapter = new LoggingIoAdapter(rafAdapter, logFileName(itemCount));
		Configuration config = Db4o.cloneConfiguration();
		config.io(ioAdapter);
		return config;
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
