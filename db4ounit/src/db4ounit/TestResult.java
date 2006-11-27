package db4ounit;

import java.io.IOException;
import java.io.Writer;

import db4ounit.util.StopWatch;

public class TestResult extends Printable {

	private TestFailureCollection _failures = new TestFailureCollection();
	
	private int _testCount = 0;
	
	private final StopWatch _watch = new StopWatch();
	
	private final Writer _stdout;
	
	public TestResult(boolean printLabels) {
		_stdout = printLabels ? TestPlatform.getStdOut() : null;
	}
	
	public TestResult() {
		this(false);
	}

	public void testStarted(Test test) throws IOException {		
		++_testCount;
		printLabel(test.getLabel());
	}

	private void printLabel(String label) throws IOException {
		if (null != _stdout) {
			_stdout.write(label + "\n");
			_stdout.flush();
		}
	}
	
	public void testFailed(Test test, Throwable failure) {
		_failures.add(new TestFailure(test, failure));
	}
	
	public boolean green() {
		return _failures.size() == 0;
	}

	public TestFailureCollection failures() {
		return _failures;
	}
	
	public void print(Writer writer) throws IOException {		
		if (green()) {
			writer.write("GREEN (" + _testCount + " tests) - " + elapsedString() + "\n");
			return;
		}
		writer.write("RED (" + _failures.size() +" out of " + _testCount + " tests failed) - " + elapsedString() + "\n");				
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
}
