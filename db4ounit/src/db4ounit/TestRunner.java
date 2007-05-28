package db4ounit;

import java.io.IOException;

public class TestRunner {
	
	private TestSuiteBuilder _suiteBuilder;
	
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
		return run(true);
	}

	private int run(boolean printLabels) {
		TestSuite suite = buildTestSuite();
		if (null == suite) return 1;
		
		TestResult result = new TestResult(printLabels);
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
		TestPlatform.printStackTrace(TestPlatform.getStdOut(), x);
	}

	private void report(TestResult result) {
		reportToTextFile(result);
		reportToStdErr(result);
	}

	private void reportToTextFile(TestResult result) {
		try {
			java.io.Writer writer = TestPlatform.openTextFile("db4ounit.log");
			try {
				report(writer, result);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			report(e);
		}
	}

	private void reportToStdErr(TestResult result) {
		report(TestPlatform.getStdErr(), result);
	}

	private void report(java.io.Writer writer, TestResult result) {
		try {
			result.print(writer);
			writer.flush();
		} catch (IOException e) {
			report(e);
		}
	}
}
