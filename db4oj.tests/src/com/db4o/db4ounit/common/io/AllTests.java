/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.io;

import com.db4o.foundation.*;

import db4ounit.ReflectionTestSuiteBuilder;
import db4ounit.ConsoleTestRunner;
import db4ounit.TestSuiteBuilder;

public class AllTests implements TestSuiteBuilder {

	public Iterator4 iterator() {
		return new ReflectionTestSuiteBuilder(new Class[] {
				IoAdapterTest.class
			}).iterator();	
	}

	public static void main(String[] args) {
		new ConsoleTestRunner(AllTests.class).run();
	}

}
