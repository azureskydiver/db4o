package com.db4o.test.unit.test;

import com.db4o.test.unit.*;

public class RunsRed extends TestCase {
	private RuntimeException _exception;
	
	public RunsRed(RuntimeException exception) {
		_exception=exception;
	}
	
	protected void run() {
		throw _exception;
	}
}
