/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.osgi.test;

import org.osgi.framework.*;

import db4ounit.*;
import db4ounit.extensions.*;

class Db4oTestServiceImpl implements Db4oTestService {
	
	private BundleContext _context;

	public Db4oTestServiceImpl(BundleContext context) {
		_context = context;
	}

	public int runTests(String databaseFilePath) throws Exception {
		Db4oOSGiBundleFixture fixture = new Db4oOSGiBundleFixture(_context, databaseFilePath);
		Class testClass = 
				com.db4o.db4ounit.jre12.AllTestsJdk1_2.class;
		TestSuite suite = new Db4oTestSuiteBuilder(fixture, testClass).build();
		return new TestRunner(suite).run();
	}

}
