package db4ounit;

import java.util.Enumeration;
import java.util.Vector;

public class TestFailureCollection {
	
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
}
