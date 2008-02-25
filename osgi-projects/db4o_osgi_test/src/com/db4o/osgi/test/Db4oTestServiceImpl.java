/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.osgi.test;

import org.osgi.framework.*;

import com.db4o.test.nativequery.*;
import com.db4o.test.nativequery.analysis.*;
import com.db4o.test.nativequery.cats.*;
import com.db4o.test.nativequery.expr.*;
import com.db4o.test.nativequery.expr.build.*;

import db4ounit.*;
import db4ounit.extensions.*;

class Db4oTestServiceImpl implements Db4oTestService {
	
	private BundleContext _context;

	public Db4oTestServiceImpl(BundleContext context) {
		_context = context;
	}

	public int runTests(String databaseFilePath) throws Exception {
		final Db4oOSGiBundleFixture fixture = new Db4oOSGiBundleFixture(_context, databaseFilePath);
		final Db4oTestSuiteBuilder suite = new Db4oTestSuiteBuilder(fixture, 
				new Class[] {
				ExpressionBuilderTestCase.class,
				BloatExprBuilderVisitorTestCase.class,
				ExpressionTestCase.class,
				BooleanReturnValueTestCase.class,
				NQRegressionTestCase.class,
				NQCatConsistencyTestCase.class,
				com.db4o.db4ounit.jre12.AllTestsJdk1_2.class
			});
		return new ConsoleTestRunner(suite).run();
	}

}
