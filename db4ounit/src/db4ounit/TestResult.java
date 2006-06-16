package db4ounit;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

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
			writer.write("GREEN (" + _testCount + " tests)");
			return;
		}
		writer.write("RED (" + _failures.size() +" out of " + _testCount + " tests failed)\n");				
		int index = 1;
		Enumeration iter = _failures.iterator();
		while (iter.hasMoreElements()) {
			writer.write(String.valueOf(index));
			writer.write(") ");
			((Printable)iter.nextElement()).print(writer);
			writer.write("\n");
			++index;
		}
	}

	public int assertions() {
		return 0;
	}
}
