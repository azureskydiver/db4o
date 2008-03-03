/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit;

import com.db4o.foundation.*;

public class TestRunner {
	
	private final Iterable4 _tests;

	public TestRunner(Iterable4 tests) {
		_tests = tests;
	}

	public void run(TestListener listener) {
		
		listener.runStarted();
		
		final Iterator4 iterator = _tests.iterator();
		while (iterator.moveNext()) {
			Test test = (Test)iterator.current();
			listener.testStarted(test);
			try {
				test.run();
			} catch (TestException x) {
			    Throwable reason = x.getReason();
				listener.testFailed(test, reason == null ? x : reason);
			} catch (Exception failure) {
				listener.testFailed(test, failure);
			}
		}
		
		listener.runFinished();
	}

}
