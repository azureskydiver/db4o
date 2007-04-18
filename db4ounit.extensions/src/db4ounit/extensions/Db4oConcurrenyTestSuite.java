/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package db4ounit.extensions;

import db4ounit.*;
import db4ounit.extensions.concurrency.*;

public abstract class Db4oConcurrenyTestSuite extends AbstractDb4oTestCase implements TestSuiteBuilder {

	public TestSuite build() {
		return new Db4oConcurrencyTestSuiteBuilder(fixture(), testCases()).build();
	}

	protected abstract Class[] testCases();
}
