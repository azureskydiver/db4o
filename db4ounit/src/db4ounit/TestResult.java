package db4ounit;

import java.io.IOException;
import java.io.Writer;

import db4ounit.util.StopWatch;

public class TestResult extends Printable {

	private TestFailureCollection _failures = new TestFailureCollection();
	
	private int _testCount = 0;
	
	private final StopWatch _watch = new StopWatch();
	
	private final Writer _writer;
	
	public TestResult(Writer writer) {
		_writer = writer;
	}
	
	public TestResult() {
		this(TestPlatform.getNullWriter());
	}

	public void testStarted(Test test) {		
		++_testCount;
		print(test.getLabel());
	}	
	
	public void testFailed(Test test, Throwable failure) {
		printFailure(failure);
		_failures.add(new TestFailure(test, failure));
	}
	
	private void printFailure(Throwable failure) {
		if (failure == null) {
			print("\t!");
		} else {
			print("\t! " + failure);
		}
	}

	public boolean green() {
		return _failures.size() == 0;
	}

	public TestFailureCollection failures() {
		return _failures;
	}
	
	public void print(Writer writer) throws IOException {		
		if (green()) {
			writer.write("GREEN (" + _testCount + " tests) - " + elapsedString() + TestPlatform.NEW_LINE);
			return;
		}
		writer.write("RED (" + _failures.size() +" out of " + _testCount + " tests failed) - " + elapsedString() + TestPlatform.NEW_LINE);				
		_failures.print(writer);
	}
	
	private String elapsedString() {
		return _watch.toString();
	}

	public int assertions() {
		return 0;
	}

	public void runStarted() {
		_watch.start();
	}

	public void runFinished() {
		_watch.stop();
	}
	
	private void print(String message) {
		try {
			_writer.write(message + TestPlatform.NEW_LINE);
			_writer.flush();
		} catch (IOException x) {
			TestPlatform.printStackTrace(_writer, x);
		}
	}
}
