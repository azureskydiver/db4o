package db4ounit;

import java.io.PrintWriter;
import java.util.Enumeration;

public class TestResult extends Printable {
	private TestFailureCollection _failures = new TestFailureCollection();
	
	public void testFailed(Test test, Throwable failure) {
		_failures.add(new TestFailure(test, failure));
	}
	
	public boolean green() {
		return _failures.size() == 0;
	}

	public TestFailureCollection failures() {
		return _failures;
	}
	
	public void print(PrintWriter writer) {		
		if (green()) {
			writer.write("GREEN");
			return;
		}
		writer.println("RED (" + _failures.size() +")");				
		int index = 1;
		Enumeration iter = _failures.iterator();
		while (iter.hasMoreElements()) {
			writer.print(index);
			writer.print(") ");
			((Printable)iter.nextElement()).print(writer);
			writer.println();
			++index;
		}
	}

	public int assertions() {
		return 0;
	}
}
