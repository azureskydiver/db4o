/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.osgi.test;

import org.osgi.framework.BundleContext;

import com.db4o.test.nativequery.NQRegressionTestCase;
import com.db4o.test.nativequery.analysis.BloatExprBuilderVisitorTestCase;
import com.db4o.test.nativequery.analysis.BooleanReturnValueTestCase;
import com.db4o.test.nativequery.cats.NQCatConsistencyTestCase;
import com.db4o.test.nativequery.expr.ExpressionTestCase;
import com.db4o.test.nativequery.expr.build.ExpressionBuilderTestCase;

import db4ounit.TestRunner;
import db4ounit.TestSuite;
import db4ounit.extensions.Db4oTestSuiteBuilder;

class Db4oTestServiceImpl implements Db4oTestService {
	
	private BundleContext _context;

	public Db4oTestServiceImpl(BundleContext context) {
		_context = context;
	}

	public int runTests(String databaseFilePath) throws Exception {
		Db4oOSGiBundleFixture fixture = new Db4oOSGiBundleFixture(_context, databaseFilePath);
		TestSuite suite = new Db4oTestSuiteBuilder(fixture, 
				new Class[] {
				ExpressionBuilderTestCase.class,
				BloatExprBuilderVisitorTestCase.class,
				ExpressionTestCase.class,
				BooleanReturnValueTestCase.class,
				NQRegressionTestCase.class,
				NQCatConsistencyTestCase.class,
				com.db4o.db4ounit.jre12.AllTestsJdk1_2.class
			}).build();
		return new TestRunner(suite).run();
	}

}
