/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit;


public class DeferredTest implements TestDecoration {
	
	private final TestFactory _factory;
	private Test _test;

	public DeferredTest(TestFactory factory) {
		_factory = factory;
	}

	public String getLabel() {
		return test().getLabel();
	}

	public void run() {
		test().run();
	}
	
	public Test test() {
		if (_test == null) {
			_test = _factory.newInstance();
		}
		return _test;
	}
}
