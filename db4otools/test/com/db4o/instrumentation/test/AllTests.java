package com.db4o.instrumentation.test;

import com.db4o.foundation.*;
import com.db4o.instrumentation.test.core.*;

import db4ounit.*;

public class AllTests implements TestSuiteBuilder {

	public Iterator4 iterator() {
		return new ReflectionTestSuiteBuilder(new Class[] {
				DefaultFilePathRootTestCase.class,
			}).iterator();	
	}

	public static void main(String[] args) {
		new ConsoleTestRunner(AllTests.class).run();
	}
}
