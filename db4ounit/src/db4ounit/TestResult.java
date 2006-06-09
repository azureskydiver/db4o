package db4ounit;

import java.io.IOException;
import java.io.Writer;
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
	
	public void print(Writer writer) throws IOException {		
		if (green()) {
			writer.write("GREEN");
			return;
		}
		writer.write("RED (" + _failures.size() +")\n");				
		int index = 1;
		Enumeration iter = _failures.iterator();
		while (iter.hasMoreElements()) {
			writer.write(index);
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
