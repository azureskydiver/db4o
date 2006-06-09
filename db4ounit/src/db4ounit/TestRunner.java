package db4ounit;

import java.io.PrintWriter;

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

	public void run() {
		TestResult result = new TestResult();
		_suite.run(result);
		report(result);
	}

	private void report(TestResult result) {
		PrintWriter writer = new PrintWriter(System.out);
		result.print(writer);
		writer.flush();
	}
}
