package db4ounit;

import java.io.IOException;

public class TestRunner {
	
	TestSuite _suite;
	
	public TestRunner(TestSuite suite) {
		if (null == suite) throw new IllegalArgumentException("suite");
		_suite = suite;
	}
	
	public TestRunner(TestSuiteBuilder builder) {
		if (null == builder) throw new IllegalArgumentException("suite");
		_suite = builder.build();
	}
	
	public TestRunner(Class clazz) {
		this(new ReflectionTestSuiteBuilder(clazz));
	}

	public int run() {
		TestResult result = new TestResult();
		_suite.run(result);
		report(result);
		return result.failures().size();
	}

	private void report(TestResult result) {
		try {
			java.io.Writer stdout = TestPlatform.getStdOut();
			result.print(stdout);
			stdout.flush();
		} catch (IOException e) {
		}
	}
}
