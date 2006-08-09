package db4ounit.db4o;

import db4ounit.TestRunner;
import db4ounit.TestSuite;
import db4ounit.TestSuiteBuilder;
import db4ounit.db4o.fixtures.Db4oSolo;

/**
 * Base class for composable db4o test suites (AllTests classes inside each package, for instance).
 */
public abstract class Db4oTestSuite extends Db4oTestCase implements TestSuiteBuilder {

	public TestSuite build() {
		return new Db4oTestSuiteBuilder(fixture(), testCases()).build();
	}

	protected abstract Class[] testCases();
	
	public int runSolo() {
		return new TestRunner(
					new Db4oTestSuiteBuilder(
							new Db4oSolo(), testCases())).run();
	}
}
