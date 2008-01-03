/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.taexamples;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.diagnostic.Diagnostic;
import com.db4o.diagnostic.DiagnosticListener;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ta.NotTransparentActivationEnabled;
import com.db4o.ta.TransparentActivationSupport;

public class TAExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		testActivation();
		testCollectionActivation();
	}

	// end main

	private static void storeSensorPanel() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database(Db4o.newConfiguration());
		if (container != null) {
		try {
			// create a linked list with length 10
			SensorPanelTA list = new SensorPanelTA().createList(10);
			container.store(list);
		} finally {
			closeDatabase();
		}
		}
	}

	// end storeSensorPanel

	private static void activateDiagnostics(Configuration configuration) {
		// Add diagnostic listener that will show all the classes that are not
		// TA aware.
		configuration.diagnostic().addListener(new DiagnosticListener() {
			public void onDiagnostic(Diagnostic diagnostic) {
				if (!(diagnostic instanceof NotTransparentActivationEnabled)) {
					return;
				}
				System.out.println(diagnostic.toString());
			}
		});
	}
	// end activateDiagnostics

	private static Configuration configureTA() {
		Configuration configuration = Db4o.newConfiguration();
		// add TA support
		configuration.add(new TransparentActivationSupport());
		// activate TA diagnostics to reveal the classes that are not TA-enabled.
		activateDiagnostics(configuration);
		return configuration;	
	}
	// end configureTA
	
	private static void testActivation() {
		storeSensorPanel();
		Configuration configuration = configureTA();

		ObjectContainer container = database(configuration);
		if (container != null) {
		try {
			ObjectSet result = container.queryByExample(new SensorPanelTA(1));
			listResult(result);
			if (result.size() > 0) {
				SensorPanelTA sensor = (SensorPanelTA) result.get(0);
				// the object is a linked list, so each call to next()
				// will need to activate a new object
				SensorPanelTA next = sensor.getNext();
				while (next != null) {
					System.out.println(next);
					next = next.getNext();
				}
			}
		} finally {
			closeDatabase();
		}
		} 
	}

	// end testActivation

	private static void storeCollection() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database(configureTA());
		if (container != null) {
			try {
				Team team = new Team();
				for (int i = 0; i < 10; i++) {
					team.addPilot(new Pilot("Pilot #" + i));
				}
				container.store(team);
				container.commit();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				closeDatabase();
			}
		}
	}

	// end storeCollection

	private static void testCollectionActivation() {
		storeCollection();
		ObjectContainer container = database(configureTA());
		if (container != null) {
			try {
				Team team = (Team) container.queryByExample(new Team()).next();
				// this method will activate all the members in the collection 
				team.listAllPilots();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				closeDatabase();
			}
		}
	}

	// end testCollectionActivation

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
