/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.foundation;

import db4ounit.ReflectionTestSuiteBuilder;
import db4ounit.TestRunner;
import db4ounit.TestSuite;
import db4ounit.TestSuiteBuilder;


public class AllTests implements TestSuiteBuilder {
	
	public TestSuite build() {
		return new ReflectionTestSuiteBuilder(new Class[] {
			ArrayIterator4TestCase.class,
			Collection4TestCase.class,
			Hashtable4TestCase.class,
			BitMap4TestCase.class,
			YapReaderTestCase.class,
		}).build();	
	}
	
	public static void main(String[] args) {
		new TestRunner(AllTests.class).run();
	}

}
