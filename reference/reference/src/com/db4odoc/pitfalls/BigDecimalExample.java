/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.pitfalls;

import java.io.*;
import java.math.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;


public class BigDecimalExample {
	
private final static String DB4O_FILE_NAME = "reference.db4o";
	
	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		Configuration configuration = Db4o.newConfiguration();
		// Try to store a BigDecimal value using 
		// the default configuration
		storeBigDecimal(configuration);
		retrieveBigDecimal(configuration);
		configuration = configure();
		// Use specific configuration to store BigDecimal
		storeBigDecimal(configuration);
		retrieveBigDecimal(configuration);
	}
	// end main
	
	private static Configuration configure() {
		Configuration configuration = Db4o.newConfiguration();
		//configuration.objectClass(BigDecimal.class).callConstructor(true);
		configuration.objectClass(BigDecimal.class).storeTransientFields(true);
		return configuration;
	}
	// end configure


	public static void storeBigDecimal(Configuration configuration) {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database(configuration);
		if (container != null) {
			try {
				BigDecimal d = new BigDecimal("-918.099995e-15"); 
				System.out.println("BigDecimal value: " + d);

				container.store(d); 
				
				container.commit();
		} catch (Exception ex) {
				System.out.println("Exception: " + ex.toString());
			} finally {
				closeDatabase();
			}
		}
	}
	// end storeBigDecimal
	
	public static void retrieveBigDecimal(Configuration configuration) {
		ObjectContainer container = database(configuration);
		if (container != null) {
			try {
				ObjectSet result = container.query(BigDecimal.class);
				listResult(result);
				
			} catch (Exception ex) {
				System.out.println("Exception: " + ex.toString());
			} finally {
				closeDatabase();
			}
		}
	}
	// end retrieveBigDecimal
	
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
