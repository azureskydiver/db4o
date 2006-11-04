package db4ounit;

import java.io.IOException;
import java.io.Writer;

public class TestRunner {
	
	TestSuiteBuilder _suiteBuilder;
	
	public TestRunner(TestSuite suite) {
		if (null == suite) throw new IllegalArgumentException("suite");
		_suiteBuilder = new NullTestSuiteBuilder(suite);
	}
	
	public TestRunner(TestSuiteBuilder builder) {
		if (null == builder) throw new IllegalArgumentException("suite");
		_suiteBuilder = builder;
	}
	
	public TestRunner(Class clazz) {
		this(new ReflectionTestSuiteBuilder(clazz));
	}

	public int run() {
		TestSuite suite = buildTestSuite();
		if (null == suite) return 1;
		
		TestResult result = new TestResult();
		result.runStarted();
		suite.run(result);
		result.runFinished();
		report(result);
		return result.failures().size();
	}
	
	private TestSuite buildTestSuite() {
		try {
			return _suiteBuilder.build();
		} catch (Exception x) {
			report(x);
		}
		return null;
	}

	private void report(Exception x) {
		Writer stdout = TestPlatform.getStdOut();
		TestPlatform.printStackTrace(stdout, x);
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
