/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit;

import java.lang.reflect.*;

/**
 * Implements the {@link Test} protocol.
 */
public abstract class TestAdapter implements Test {

	public TestAdapter() {
		super();
	}
	
	protected abstract void runTest() throws Exception;

	protected void setUp() {
	}

	protected void tearDown() {
	}	

	public void run(TestResult result) {
		try {
			result.testStarted(this);
			setUp();
			runTest();
		} catch (InvocationTargetException e) {
			result.testFailed(this, e.getTargetException());
		} catch (Exception e) {
			result.testFailed(this, e);
		} finally {
			try {
				tearDown();
			} catch (TestException e) {
				result.testFailed(this, e);
			}
		}
	}

}