package com.db4o.test.unit.test;

import com.db4o.test.unit.*;

public class RunsRed extends TestCase {
	private RuntimeException _exc;
	
	public RunsRed(RuntimeException exc) {
		_exc=exc;
	}
	
	protected void run() {
		throw _exc;
	}
}
