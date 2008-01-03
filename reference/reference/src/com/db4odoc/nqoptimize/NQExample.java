/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.nqoptimize;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.diagnostic.Diagnostic;
import com.db4o.diagnostic.DiagnosticListener;
import com.db4o.diagnostic.NativeQueryNotOptimized;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.Db4oException;
import com.db4o.query.Predicate;

public class NQExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private final static int OBJECT_COUNT = 10;

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		storePilots();
		selectPilot5Points();
			}

	// end main

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

	private static void storePilots() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database(configureNQ());
		if (container != null) {
			try {
				Pilot pilot;
				for (int i = 0; i < OBJECT_COUNT; i++) {
					pilot = new Pilot("Test Pilot #" + i, i);
					container.store(pilot);
				}
				for (int i = 0; i < OBJECT_COUNT; i++) {
					pilot = new Pilot("Professional Pilot #" + (i + 10), i + 10);
					container.store(pilot);
				}
				container.commit();
			} catch (Db4oException ex) {
				System.out.println("Db4o Exception: " + ex.getMessage());
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end storePilots


	private static void selectPilot5Points() {
		ObjectContainer container = database(configureNQ());
		if (container != null) {
			try {
				List<Pilot> result = container.query(new Predicate<Pilot>() {
					public boolean match(Pilot pilot) {
						// pilots with 5 points are included in the
						// result
						return pilot.getPoints() == 5;
					}
				});
				listResult(result);
			} catch (Exception ex) {
				System.out.println("System Exception: " + ex.getMessage());
			} finally {
				closeDatabase();
			}
		}
	}

	// end selectPilot5Points
	
	private static void activateDiagnostics(Configuration configuration) {
		// Add NQ diagnostics to find out when TA is not optimized
		configuration.diagnostic().addListener(new DiagnosticListener() {
			public void onDiagnostic(Diagnostic diagnostic) {
				if (!(diagnostic instanceof NativeQueryNotOptimized)) {
					//return;
				}
				System.out.println(diagnostic.toString());
			}
		});
	}
	// end activateDiagnostics

	private static Configuration configureNQ() {
		Configuration configuration = Db4o.newConfiguration();
		// disable runtime NQ optimization
		configuration.optimizeNativeQueries(false);
		activateDiagnostics(configuration);
		return configuration;	
	}
	// end configureNQ

	private static void listResult(List result) {
		System.out.println(result.size());
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
	}

	// end listResult

	private static void listResult(Set result) {
		System.out.println(result.size());
		Iterator i = result.iterator();
		while (i.hasNext()) {
			System.out.println(i.next());
		}
	}
	// end listResult

}
