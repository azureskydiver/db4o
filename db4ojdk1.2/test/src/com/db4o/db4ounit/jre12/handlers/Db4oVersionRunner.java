/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.handlers;

import java.io.*;
import java.lang.reflect.*;

import com.db4o.db4ounit.common.handlers.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class Db4oVersionRunner extends Db4oTestSuite {

	private Class[] _testCases;

	private File[] _db4oLibs;

	private String[] _db4oLibVersions;

	public static File _testBinPath;

	private static File _archivesPath;

	static {
		globalInitialize();
	}

	private static void globalInitialize() {
		_testBinPath = getCanonicalFile(System.getProperty("db4oj.tests.bin",
				"../db4oj.tests/bin"));
		_archivesPath = getCanonicalFile(System.getProperty(
				"db4o.archives.path", "../db4o.archives/java1.2/"));
	}

	private static File getCanonicalFile(String path) {
		File file = new File(path);
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			return file;
		}
	}

	public static void main(String[] args) throws Exception {
		new Db4oVersionRunner().runSolo();
	}

	protected Class[] testCases() {
		return new Class[] { 
            BooleanHandlerUpdateTestCase.class,
            ByteHandlerUpdateTestCase.class,
            CharHandlerUpdateTestCase.class,
            DateHandlerUpdateTestCase.class,
            DoubleHandlerUpdateTestCase.class,
            FloatHandlerUpdateTestCase.class,
            IntHandlerUpdateTestCase.class,
            LongHandlerUpdateTestCase.class,
            NestedArrayUpdateTestCase.class,
            ObjectArrayUpdateTestCase.class,
            ShortHandlerUpdateTestCase.class,
            StringHandlerUpdateTestCase.class, 
		};
	}

	public Db4oVersionRunner() {
		this(null, null);
	}

	public Db4oVersionRunner(String db4oLib, Class testCase) {
		if (db4oLib == null) {
			_db4oLibs = Db4oVersionUpdateService.getDb4oLibFiles(_archivesPath);
		} else {
			_db4oLibs = new File[] { getCanonicalFile(db4oLib) };
		}
		_db4oLibVersions = libVersions(_db4oLibs);
		_testCases = testCase == null ? testCases() : new Class[] { testCase };
	}

	private static String[] libVersions(File[] libs) {
		String[] versions = new String[libs.length];
		try {
			for (int i = 0; i < versions.length; ++i) {
				versions[i] = Db4oVersionUpdateService.getDb4oVersion(libs[i]
						.toURL());
			}
		} catch (Exception e) {
			Assert.fail("failed to get lib version" + e.getMessage());
		}
		return versions;
	}

	public int runSolo() {
		int failures = 0;
		for (int i = 0; i < _testCases.length; i++) {
			assertVersionUpdateTestCase(_testCases[i]);
			failures += runSingleUpdateTest(_testCases[i]);
		}
		return failures;
	}

	private void assertVersionUpdateTestCase(Class clazz) {
		if (!FormatMigrationTestCaseBase.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException();
		}
	}

	private int runSingleUpdateTest(Class test) {
		try {
			for (int i = 0; i < _db4oLibs.length; i++) {
				Db4oVersionUpdateService.createDatabase(_db4oLibs[i], test);
			}
			return run(_db4oLibVersions, test);

		} catch (Exception e) {
			throw new Db4oException(e);
		}
	}

	private int run(String[] versions, Class test) throws Exception {
		Field field = test.getField("db4oVersions");
		field.set(null, versions);
		return new TestRunner(test).run();
	}

}
