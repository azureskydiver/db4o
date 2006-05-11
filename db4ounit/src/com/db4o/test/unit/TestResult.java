package com.db4o.test.unit;

import com.db4o.foundation.*;

public class TestResult {
	private Collection4 _failures=new Collection4();
	
	public void fail(Exception exc) {
		_failures.add(exc);
	}
	
	public boolean ok() {
		return _failures.size()==0;
	}

	public Collection4 failures() {
		return _failures;
	}
	
	public String toString() {
		if(ok()) {
			return "GREEN";
		}
		String ret="RED ("+_failures.size()+")\n";
		Iterator4 iter=_failures.strictIterator();
		while(iter.hasNext()) {
			ret+=(iter.next()+"\n");
		}
		return ret;
	}

	public int assertions() {
		return 0;
	}
}
