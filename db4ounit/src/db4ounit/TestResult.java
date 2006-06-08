package db4ounit;

import java.io.PrintWriter;

import com.db4o.foundation.*;

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
		Iterator4 iter = _failures.iterator();
		while (iter.hasNext()) {
			writer.print(index);
			writer.print(") ");
			((Printable)iter.next()).print(writer);
			writer.println();
			++index;
		}
	}

	public int assertions() {
		return 0;
	}
}
