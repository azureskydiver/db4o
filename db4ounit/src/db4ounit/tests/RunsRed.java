/**
 * 
 */
package db4ounit.tests;

import db4ounit.Test;
import db4ounit.TestResult;

class RunsRed implements Test {
	private RuntimeException _exception;
	
	public RunsRed(RuntimeException exception) {
		_exception=exception;
	}

	public String getLabel() {
		return "RunsRed";
	}

	public void run(TestResult result) {
		result.testFailed(this, _exception);
	}
}