package db4ounit;

import java.io.*;

import com.db4o.foundation.*;

public class ConsoleTestRunner {
	
	private final Iterable4 _suite;
	private final boolean _reportToFile;
	
	public ConsoleTestRunner(Iterator4 suite) {
		this(suite, true);
	}

	public ConsoleTestRunner(Iterator4 suite, boolean reportToFile) {
		if (null == suite) throw new IllegalArgumentException("suite");
		_suite = Iterators.iterable(suite);
		_reportToFile = reportToFile;
	}

	public ConsoleTestRunner(Iterable4 suite) {
		if (null == suite) throw new IllegalArgumentException("suite");
		_suite = suite;
		_reportToFile = true;
	}
	
	public ConsoleTestRunner(Class clazz) {
		this(new ReflectionTestSuiteBuilder(clazz));
	}	

	public int run() {
		return run(TestPlatform.getStdErr());
	}

	public int run(Writer writer) {
		Iterator4 suite = buildTestSuite();
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
		return _suite.iterator();
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
