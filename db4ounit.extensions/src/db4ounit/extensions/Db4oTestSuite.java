package db4ounit.extensions;

import db4ounit.TestSuite;
import db4ounit.TestSuiteBuilder;

/**
 * Base class for composable db4o test suites (AllTests classes inside each package, for instance).
 */
public abstract class Db4oTestSuite extends Db4oTestCase implements TestSuiteBuilder {

	public TestSuite build() {
		return new Db4oTestSuiteBuilder(fixture(), testCases()).build();
	}

	protected abstract Class[] testCases();
}
