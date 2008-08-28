/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre5;

import com.db4o.db4ounit.jre5.concurrency.query.*;

import db4ounit.extensions.*;


/**
 * @decaf.ignore
 */
public class AllTestsConcurrency extends Db4oConcurrencyTestSuite {
	
	public static void main(String[] args) {
		System.exit(new AllTestsConcurrency().runConcurrencyAll());
	}

	protected Class[] testCases() {
		return new Class[] { 
			com.db4o.db4ounit.jre11.concurrency.AllTests.class,
			com.db4o.db4ounit.jre5.concurrency.collections.AllTests.class,
			ConcurrentLazyQueriesTestCase.class,
		};
	}

}
