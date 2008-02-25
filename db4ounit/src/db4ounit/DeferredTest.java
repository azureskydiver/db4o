/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit;


public class DeferredTest implements Test {
	
	private final TestFactory _factory;
	private Test _test;

	public DeferredTest(TestFactory factory) {
		_factory = factory;
	}

	public String getLabel() {
		return test().getLabel();
	}

	public void run(TestResult result) {
		test().run(result);
	}
	
	public Test test() {
		if (_test == null) {
			_test = _factory.newInstance();
		}
		return _test;
	}
}
