/* Copyright (C) 2009  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.diagnostics;

import java.io.IOException;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.*;
import com.db4o.diagnostic.*;
import com.db4o.io.MemoryStorage;
import com.db4o.query.Predicate;

import db4ounit.*;
import db4ounit.extensions.ExcludingReflector;
import db4ounit.extensions.fixtures.OptOutMultiSession;

public class MissingClassTestCase implements TestCase, TestLifeCycle, OptOutMultiSession {

	private static final String DB_URI = "test_db";

	private static final int PORT = 0xdb40;

	private static final String USER = "user";
	private static final String PASSWORD = "password";

	private MemoryStorage _storage = new MemoryStorage();

	public static void main(String[] args) {
		new ConsoleTestRunner(MissingClassTestCase.class).run();
	}

	public static class Pilot {
		private String name;
		private List cars = new ArrayList();

		public Pilot(String name) {
			super();
			this.name = name;
		}

		public List getCars() {
			return cars;
		}

		public String getName() {
			return name;
		}

		public String toString() {
			return "Pilot[" + name + "]";
		}
	}

	public static class Car {
		public String model;

		public Car(String model) {
			this.model = model;
		}

		public String getModel() {
			return model;
		}

		public String toString() {
			return "Car[" + model + "]";
		}
	}

	private void prepare(FileConfiguration fileConfig, CommonConfiguration commonConfig, final List classesNotFound) {
		fileConfig.storage(_storage);
		prepareDiagnostic(commonConfig, classesNotFound);
	}

	private void prepareDiagnostic(CommonConfiguration common, final List classesNotFound) {
		common.diagnostic().addListener(new DiagnosticListener() {
			public void onDiagnostic(Diagnostic d) {
				if (d instanceof MissingClass) {
					classesNotFound.add(((MissingClass) d).reason());
				}
			}
		});
	}

	public void testEmbedded() {

		List missingClasses = new ArrayList();

		EmbeddedConfiguration excludingConfig = Db4oEmbedded.newConfiguration();
		prepare(excludingConfig.file(), excludingConfig.common(), missingClasses);

		excludeClasses(excludingConfig.common(), Pilot.class, Car.class);

		EmbeddedObjectContainer excludingContainer = Db4oEmbedded.openFile(excludingConfig, DB_URI);

		try {
			excludingContainer.query(new Predicate() {
				@Override
				public boolean match(Object candidate) {
					return true;
				}
			});
		} finally {
			excludingContainer.close();
		}

		assertPilotAndCarMissing(missingClasses);

	}

	private void assertPilotAndCarMissing(List classesNotFound) {

		List<String> excluded = Arrays.asList(Pilot.class.getName(), Car.class.getName());

		IteratorAssert.sameContent(excluded, classesNotFound);
	}

	public void testMissingClassesInServer() {

		List serverMissedClasses = new ArrayList();
		List clientMissedClasses = new ArrayList();

		ServerConfiguration serverConfig = Db4oClientServer.newServerConfiguration();
		prepare(serverConfig.file(), serverConfig.common(), serverMissedClasses);

		excludeClasses(serverConfig.common(), Pilot.class, Car.class);

		ObjectServer server = Db4oClientServer.openServer(serverConfig, DB_URI, PORT);
		server.grantAccess(USER, PASSWORD);
		try {
			ClientConfiguration clientConfig = Db4oClientServer.newClientConfiguration();

			prepareDiagnostic(clientConfig.common(), clientMissedClasses);
			ObjectContainer client = Db4oClientServer.openClient(clientConfig, "localhost", PORT, USER, PASSWORD);

			client.query(new Predicate() {

				@Override
				public boolean match(Object candidate) {
					return true;
				}
			});

			client.close();
		} finally {
			server.close();
		}

		assertPilotAndCarMissing(serverMissedClasses);
		Assert.isTrue(clientMissedClasses.isEmpty());

	}

	public void testMissingClassesInClient() {

		List serverMissedClasses = new ArrayList();
		List clientMissedClasses = new ArrayList();

		ServerConfiguration serverConfig = Db4oClientServer.newServerConfiguration();
		prepare(serverConfig.file(), serverConfig.common(), serverMissedClasses);

		ObjectServer server = Db4oClientServer.openServer(serverConfig, DB_URI, PORT);
		server.grantAccess(USER, PASSWORD);
		try {
			ClientConfiguration clientConfig = Db4oClientServer.newClientConfiguration();
			excludeClasses(clientConfig.common(), Pilot.class, Car.class);
			prepareDiagnostic(clientConfig.common(), clientMissedClasses);
			ObjectContainer client = Db4oClientServer.openClient(clientConfig, "localhost", PORT, USER, PASSWORD);

			client.query(new Predicate() {

				@Override
				public boolean match(Object candidate) {
					return true;
				}
			});

			client.close();
		} finally {
			server.close();
		}

		Assert.isTrue(serverMissedClasses.isEmpty());
		assertPilotAndCarMissing(clientMissedClasses);

	}

	private void excludeClasses(CommonConfiguration commonConfiguration, Class<?>... classes) {

		commonConfiguration.reflectWith(new ExcludingReflector(classes));
	}
	
	public void testClassesFound() throws IOException {

		List missingClasses = new ArrayList();
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		prepare(config.file(), config.common(), missingClasses);
		populateContainer(config);
		Assert.isTrue(missingClasses.isEmpty());

	}

	private void populateContainer(EmbeddedConfiguration config) {
		config.file().storage(_storage);
		ObjectContainer container = Db4oEmbedded.openFile(config, DB_URI);

		try {
			Pilot pilot = new Pilot("Barrichello");
			pilot.getCars().add(new Car("BMW"));
			container.store(pilot);
		} finally {
			container.close();
		}
	}

	public void setUp() throws Exception {

		populateContainer(Db4oEmbedded.newConfiguration());
	}

	public void tearDown() throws Exception {
	}

}
