/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit;


public interface TestListener {
	
	void runStarted();
	
	void testStarted(Test test);
	
	void testFailed(Test test, Throwable failure);
	
	void runFinished();

}
