package db4ounit.tests;

import db4ounit.*;

public class RunsRed extends TestCase {
	private RuntimeException _exception;
	
	public RunsRed(RuntimeException exception) {
		_exception=exception;
	}
	
	protected void run() {
		throw _exception;
	}
}
