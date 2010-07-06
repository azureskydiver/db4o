/* Copyright (C) 2008   Versant Inc.   http://www.db4o.com */

package db4ounit;

public class TestDecorationAdapter implements Test {

	private final Test _test;
	
	public TestDecorationAdapter(Test test) {
		_test = test;
	}
	
	public String label() {
		return _test.label();
	}

	public void run() {
		_test.run();
	}

	public boolean isLeafTest() {
		return _test.isLeafTest();
	}

}
