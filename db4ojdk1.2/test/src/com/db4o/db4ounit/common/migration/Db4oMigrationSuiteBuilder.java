/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.migration;

import java.io.*;

import com.db4o.db4ounit.common.handlers.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;

public class Db4oMigrationSuiteBuilder extends ReflectionTestSuiteBuilder {
	
	/**
	 * Runs the tests against all archived libraries + the current one
	 */
	public static final String[] ALL = null;
	
	/**
	 * Runs the tests against the current version only.
	 */
	public static final String[] CURRENT = new String[0];
	
	private final Db4oLibraryEnvironmentProvider _environmentProvider = new Db4oLibraryEnvironmentProvider(PathProvider.testCasePath());
	private final String[] _specificLibraries;

	/**
	 * Creates a suite builder for the specific FormatMigrationTestCaseBase derived classes
	 * and specific db4o libraries. If no libraries are specified (either null or empty array)
	 * {@link Db4oLibrarian#libraries} is used to find archived libraries.
	 * 
	 * @param classes
	 * @param specificLibraries
	 */
	public Db4oMigrationSuiteBuilder(Class[] classes, String[] specificLibraries) {
		super(classes);
		_specificLibraries = specificLibraries;
	}
	
	protected Iterator4 fromClass(Class clazz) {
		assertMigrationTestCase(clazz);
		final Iterator4 defaultTestSuite = super.fromClass(clazz);
		try {
			final Iterator4 migrationTestSuite = migrationTestSuite(clazz, db4oLibraries());
			return Iterators.concat(migrationTestSuite, defaultTestSuite);
		} catch (Exception e) {
			return Iterators.concat(Iterators.iterateSingle(new FailingTest(clazz.getName(), e)), defaultTestSuite);
		}
	}

	private Iterator4 migrationTestSuite(final Class clazz, Db4oLibrary[] libraries) throws Exception {
		return Iterators.map(libraries, new Function4() {
			public Object apply(Object library)  {
				try {
					return migrationTest((Db4oLibrary) library, clazz);
				} catch (Exception e) {
					throw new Db4oException(e);
				}
			}
		});
	}

	private Db4oMigrationTest migrationTest(final Db4oLibrary library,
			Class clazz) throws Exception {
		final FormatMigrationTestCaseBase instance = (FormatMigrationTestCaseBase)newInstance(clazz);
		return new Db4oMigrationTest(instance, library);
	}

	private Db4oLibrary[] db4oLibraries() throws Exception {
		if (hasSpecificLibraries()) {
			return specificLibraries();
		}
		return librarian().libraries();
	}

	private Db4oLibrary[] specificLibraries() throws Exception {
		Db4oLibrary[] libraries = new Db4oLibrary[_specificLibraries.length];
		for (int i = 0; i < libraries.length; i++) {
			libraries[i] = librarian().forFile(_specificLibraries[i]);
		}
		return libraries;
	}

	private boolean hasSpecificLibraries() {
		return null != _specificLibraries;
	}

	private Db4oLibrarian librarian() {
		return new Db4oLibrarian(_environmentProvider);
	}

	private void assertMigrationTestCase(Class clazz) {
		if (!FormatMigrationTestCaseBase.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException();
		}
	}
	
	private static final class Db4oMigrationTest implements Test {

		private final FormatMigrationTestCaseBase _test;
		private final Db4oLibrary _library;
		private final String _version;

		public Db4oMigrationTest(FormatMigrationTestCaseBase test, Db4oLibrary library) throws Exception {
			_library = library;
			_test = test;
			_version = environment().version();
		}

		public String getLabel() {
			return "[" + _version + "] " + _test.getClass().getName();
		}

		public void run() {
			try {
				createDatabase();
				test();
			} catch (TestException e) {
				throw e;
			} catch (Exception e) {
				throw new TestException(e);
			}
		}

		private void test() throws IOException {
			_test.test(_version);
		}

		private void createDatabase() throws Exception {
			environment().invokeInstanceMethod(_test.getClass(), "createDatabaseFor", new Object[] { _version });
		}

		private Db4oLibraryEnvironment environment() {
			return _library.environment;
		}
	}
}
