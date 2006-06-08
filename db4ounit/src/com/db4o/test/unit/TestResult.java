package com.db4o.test.unit;

import java.io.PrintWriter;

import com.db4o.foundation.*;

public class TestResult extends Printable {
	private TestFailureCollection _failures = new TestFailureCollection();
	
	public void fail(Test test, Exception failure) {
		_failures.add(new TestFailure(test, failure));
	}
	
	public boolean ok() {
		return _failures.size() == 0;
	}

	public TestFailureCollection failures() {
		return _failures;
	}
	
	public void print(PrintWriter writer) {		
		if (ok()) {
			writer.write("GREEN");
			return;
		}
		writer.println("RED (" + _failures.size() +")");
		Iterator4 iter = _failures.iterator();
		while (iter.hasNext()) {
			((Printable)iter.next()).print(writer);
			writer.println();
		}
	}

	public int assertions() {
		return 0;
	}
}
