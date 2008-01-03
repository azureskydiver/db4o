package db4ounit;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

public class TestFailureCollection extends Printable {
	
	Vector _failures = new Vector();
	
	public Enumeration iterator() {
		return _failures.elements();
	}
	
	public int size() {
		return _failures.size();
	}
	
	public void add(TestFailure failure) {
		_failures.addElement(failure);
	}
	
	public void print(Writer writer) throws IOException {
		printSummary(writer);
		printDetails(writer);
	}

	private void printSummary(Writer writer) throws IOException {
		int index = 1;
		Enumeration e = iterator();
		while (e.hasMoreElements()) {
			writer.write(String.valueOf(index));
			writer.write(") ");
			writer.write(((TestFailure)e.nextElement()).getTest().getLabel());
			writer.write(TestPlatform.NEW_LINE);
			++index;
		}
	}

	private void printDetails(Writer writer) throws IOException {
		int index = 1;
		Enumeration e = iterator();
		while (e.hasMoreElements()) {
			writer.write(TestPlatform.NEW_LINE);
			writer.write(String.valueOf(index));
			writer.write(") ");
			((Printable)e.nextElement()).print(writer);
			writer.write(TestPlatform.NEW_LINE);
			++index;
		}
	}
}
