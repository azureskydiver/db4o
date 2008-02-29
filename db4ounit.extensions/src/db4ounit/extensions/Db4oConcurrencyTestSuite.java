/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package db4ounit.extensions;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.concurrency.*;

public abstract class Db4oConcurrencyTestSuite extends AbstractDb4oTestCase implements TestSuiteBuilder {

	public Iterator4 iterator() {
		return new Db4oConcurrencyTestSuiteBuilder(fixture(), testCases()).iterator();
	}

	protected abstract Class[] testCases();
}
