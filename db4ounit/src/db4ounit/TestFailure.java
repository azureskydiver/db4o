package db4ounit;

import java.io.IOException;
import java.io.Writer;

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
	
	public void print(Writer writer) throws IOException {
		writer.write(_test.label());
		writer.write(": ");
		// TODO: don't print the first stack trace elements
		// which reference db4ounit.Assert methods
		TestPlatform.printStackTrace(writer, _failure);
	}
}
