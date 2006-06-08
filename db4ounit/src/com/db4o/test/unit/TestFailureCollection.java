package com.db4o.test.unit;

import com.db4o.foundation.Collection4;
import com.db4o.foundation.Iterator4;

public class TestFailureCollection {
	
	Collection4 _failures = new Collection4();
	
	public Iterator4 iterator() {
		return _failures.strictIterator();
	}
	
	public int size() {
		return _failures.size();
	}
	
	public void add(TestFailure failure) {
		_failures.add(failure);
	}
}
