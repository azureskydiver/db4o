/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.mocking.*;

public class TestRunnerTestCase implements TestCase { 
	
	static final RuntimeException FAILURE_EXCEPTION = new RuntimeException();
	
	public void testRun() {
		final RunsGreen greenTest = new RunsGreen();
		final RunsRed redTest = new RunsRed(FAILURE_EXCEPTION);
		final Iterable4 tests = Iterators.iterable(new Object[] {
			greenTest,
			redTest,
		});
		
		final MethodCallRecorder recorder = new MethodCallRecorder();
		final TestListener listener = new TestListener() {
			
			public void testStarted(Test test) {
				recorder.record(new MethodCall("testStarted", test));
			}
		
			public void testFailed(Test test, Throwable failure) {
				recorder.record(new MethodCall("testFailed", test, failure));
			}
		
			public void runStarted() {
				recorder.record(new MethodCall("runStarted"));
			}
		
			public void runFinished() {
				recorder.record(new MethodCall("runFinished"));
			}
			
		};
		new TestRunner(tests).run(listener);
		
		recorder.verify(new MethodCall[] {
			new MethodCall("runStarted"),
			new MethodCall("testStarted", greenTest),
			new MethodCall("testStarted", redTest),
			new MethodCall("testFailed", redTest, FAILURE_EXCEPTION),
			new MethodCall("runFinished"),
		});
	}
	
	public void testRunWithException() {
	    Test test = new Test() {

            public String getLabel() {
                return "Test"; //$NON-NLS-1$
            }

            public void run() {
                Assert.areEqual(0, 1);
            }
	        
	    };
	    
	    Iterable4 tests = Iterators.iterable(new Object[] {
	            test,
	    });
	    try {
	        new ConsoleTestRunner(tests).run();
	    } catch (AssertionException e) {
	        //expected
	    }
	}

}
