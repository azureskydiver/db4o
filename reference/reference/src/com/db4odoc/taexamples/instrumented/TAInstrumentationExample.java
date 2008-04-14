package com.db4odoc.taexamples.instrumented;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.reflect.jdk.*;
import com.db4o.ta.*;

public class TAInstrumentationExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	private static ObjectContainer _container = null;

	public static void main(String[] args) {
		testActivation();
	}
	// end main

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
		configuration.add(new TransparentActivationSupport());
		// configure db4o to use instrumenting classloader
		// This is required for build time optimization!
		configuration.reflectWith(new JdkReflector(
				TAInstrumentationExample.class.getClassLoader()));

		return configuration;
	}
	// end configureTA

	private static void storeSensorPanel() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = database(configureTA());
		if (container != null) {
			try {
				// create a linked list with length 10
				SensorPanel list = new SensorPanel().createList(10);
				container.store(list);
			} finally {
				closeDatabase();
			}
		}
	}

	// end storeSensorPanel

	private static void testActivation() {
		storeSensorPanel();
		Configuration configuration = configureTA();
		activateDiagnostics(configuration);

		ObjectContainer container = database(configuration);
		if (container != null) {
			try {
				Query query  = container.query();
				query.constrain(SensorPanel.class);
				query.descend("_sensor").constrain(new Integer(1));
				ObjectSet result = query.execute();
				listResult(result);
				if (result.size() > 0) {
					SensorPanel sensor = (SensorPanel) result.get(0);
					SensorPanel next = sensor._next;
					while (next != null) {
						System.out.println(next);
						next = next._next;
					}
				}
			} finally {
				closeDatabase();
			}
		}
	}

	// end testActivation

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
