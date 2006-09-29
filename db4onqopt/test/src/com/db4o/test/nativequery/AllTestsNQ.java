/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery;

import com.db4o.test.nativequery.analysis.*;
import com.db4o.test.nativequery.cats.*;
import com.db4o.test.nativequery.expr.*;
import com.db4o.test.nativequery.expr.build.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class AllTestsNQ {
	
	public static void main(String[] args) {
		TestSuite plainTests=new ReflectionTestSuiteBuilder(
				new Class[] {
						ExpressionTestCase.class,
						ExpressionBuilderTestCase.class,
						BloatExprBuilderVisitorTestCase.class,
				}
		).build();
		TestSuite db4oTests=new Db4oTestSuiteBuilder(new Db4oSolo(),
					new Class[] {
						NQRegressionTestCase.class,
						//NQCatConsistencyTestCase.class,
					}
		).build();
		TestSuite allTests=new TestSuite("All native query tests",
				new Test[] {
					plainTests,
					db4oTests,
				}
		);
		int ret=new TestRunner(allTests).run();
		if(ret!=0) {
			throw new RuntimeException(ret+" tests failed");
		}
	}
}
