package db4ounit;

import java.io.PrintWriter;

public class TestFailure extends Printable {
	
	Test _test;
	Throwable _failure;
	
	public TestFailure(Test test, Throwable failure) {
		_test = test;
		_failure = failure;
	}
	
	public Test getTest() {
		return _test;
	}
	
	public Throwable getFailure() {
		return _failure;
	}
	
	public void print(PrintWriter printWriter) {
		printWriter.print(_test.getLabel());
		printWriter.print(": ");
		// TODO: don't print the first stack trace elements
		// which reference db4ounit.Assert methods
		_failure.printStackTrace(printWriter);
	}
}
