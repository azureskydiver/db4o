package db4ounit;

import java.io.IOException;
import java.io.Writer;

public class TestResult extends Printable {
	private TestFailureCollection _failures = new TestFailureCollection();
	
	private int _testCount = 0;
	
	public void testStarted(Test test) {
		++_testCount;
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
			writer.write("GREEN (" + _testCount + " tests)\n");
			return;
		}
		writer.write("RED (" + _failures.size() +" out of " + _testCount + " tests failed)\n");				
		_failures.print(writer);
	}

	public int assertions() {
		return 0;
	}
}
