package db4ounit;

import java.io.*;

import com.db4o.foundation.*;

public class TestRunner {
	
	private TestSuiteBuilder _suiteBuilder;
	private boolean _reportToFile = true;
	
	public TestRunner(Iterator4 suite) {
		this(suite, true);
	}

	public TestRunner(Iterator4 suite, boolean reportToFile) {
		if (null == suite) throw new IllegalArgumentException("suite");
		_suiteBuilder = new NullTestSuiteBuilder(suite);
		_reportToFile = reportToFile;
	}

	public TestRunner(TestSuiteBuilder builder) {
		if (null == builder) throw new IllegalArgumentException("suite");
		_suiteBuilder = builder;
	}
	
	public TestRunner(Class clazz) {
		this(new ReflectionTestSuiteBuilder(clazz));
	}	

	public int run() {
		return run(TestPlatform.getStdErr());
	}

	public int run(Writer writer) {
		Iterator4 suite = buildTestSuite();
		if (null == suite) return 1;
		
		TestResult result = new TestResult(writer);
		result.runStarted();
		runAll(result, suite);
		result.runFinished();
		reportResult(result, writer);
		return result.failures().size();
	}

	public static void runAll(TestResult result, Iterator4 tests) {
		while (tests.moveNext()) {
			((Test)tests.current()).run(result);
		}
	}
	
	private Iterator4 buildTestSuite() {
		try {
			return _suiteBuilder.build();
		} catch (Exception x) {
			report(x);
		}
		return null;
	}

	private void report(Exception x) {
		TestPlatform.printStackTrace(TestPlatform.getStdErr(), x);
	}

	private void reportResult(TestResult result, Writer writer) {
		if(_reportToFile) {
			reportToTextFile(result);
		}
		report(result, writer);
	}

	private void reportToTextFile(TestResult result) {
		try {
			java.io.Writer writer = TestPlatform.openTextFile("db4ounit.log");
			try {
				report(result, writer);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			report(e);
		}
	}

	private void report(TestResult result, Writer writer) {
		try {
			result.print(writer);
			writer.flush();
		} catch (IOException e) {
			report(e);
		}
	}
}
