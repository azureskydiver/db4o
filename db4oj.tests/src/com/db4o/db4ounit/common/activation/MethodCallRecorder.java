package com.db4o.db4ounit.common.activation;

import com.db4o.foundation.*;

public class MethodCallRecorder implements Iterable4 {
	
	private final Collection4 _calls = new Collection4();
	
	public Iterator4 iterator() {
		return _calls.iterator();
	}
	
	public void record(MethodCall call) {
		_calls.add(call);
	}
	
	public void reset() {
		_calls.clear();
	}
}
