package db4ounit.extensions;

import com.db4o.foundation.*;

import db4ounit.TestSuiteBuilder;

/**
 * Base class for composable db4o test suites (AllTests classes inside each package, for instance).
 */
public abstract class Db4oTestSuite extends AbstractDb4oTestCase implements TestSuiteBuilder {

	public Iterator4 build() {
		return new Db4oTestSuiteBuilder(fixture(), testCases()).build();
	}

	protected abstract Class[] testCases();
}
