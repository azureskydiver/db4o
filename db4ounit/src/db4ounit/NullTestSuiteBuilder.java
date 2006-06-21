package db4ounit;

public class NullTestSuiteBuilder implements TestSuiteBuilder {
	
	private TestSuite _suite;

	public NullTestSuiteBuilder(TestSuite suite) {
		_suite = suite;
	}

	public TestSuite build() {
		return _suite;
	}

}
