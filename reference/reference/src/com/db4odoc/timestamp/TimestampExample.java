/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.timestamp;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.DatabaseFileLockedException;


public class TimestampExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";
	
	private static ObjectContainer _container = null;
	
	public static void main(String[] args) {
		// try to save Timestamp with the default configuration
		Configuration configuration = Db4o.newConfiguration();
		storeTimestamp(configuration);
		retrieveTimestamp(configuration);
		//	add specific configuration and try to save Timestamp
		configuration = configure();
		storeTimestamp(configuration);
		retrieveTimestamp(configuration);
	}

	// end main
	
	private static Configuration configure() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(Date.class).storeTransientFields(true);
		return configuration;
	}
	// end configure

	private static void storeTimestamp(Configuration configuration) {
		new File(DB4O_FILE_NAME).delete();
		
		ObjectContainer container = database(configuration);
		if (container != null) {
			try {
				Timestamp timestamp = new Timestamp(System.currentTimeMillis()); 
				System.out.println("timestamp: " + timestamp);

				container.store(timestamp); 
				
				container.commit();
		} catch (Exception ex) {
				System.out.println("Exception: " + ex.toString());
			} finally {
				closeDatabase();
			}
		}
	}
	// end storeTimestamp

	private static void retrieveTimestamp(Configuration configuration) {
		ObjectContainer container = database(configuration);
		if (container != null) {
			try {
				ObjectSet result = container.query(Timestamp.class);
				listResult(result);
				
			} catch (Exception ex) {
				System.out.println("Exception: " + ex.toString());
			} finally {
				closeDatabase();
			}
		}
	}
	// end retrieveTimestamp

	
	private static ObjectContainer database(Configuration configuration) {
		if (_container == null) {
			try {
				_container = Db4o.openFile(configuration, DB4O_FILE_NAME);
			} catch (DatabaseFileLockedException ex) {
				System.out.println(ex.getMessage());
			}
		}
		return _container;
	}

	// end database

	private static void closeDatabase() {
		if (_container != null) {
			_container.close();
			_container = null;
		}
	}

	// end closeDatabase
	
	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}

	// end listResult

	
}
