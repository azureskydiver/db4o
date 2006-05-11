package com.db4o.test.unit;

public abstract class TestCase extends Assert implements Test {
	
	public void run(TestResult result) {
		try {
			setUp();
			run();
		}
		catch(Exception exc) {
			result.fail(exc);
		}
		finally {
			try {
				tearDown();
			} catch (Exception exc) {
				result.fail(exc);
			}
		}
	}
	
	protected void setUp() throws Exception {}

	protected void tearDown() throws Exception {}

	protected abstract void run() throws Exception;
}
