/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit;

import com.db4o.foundation.*;

import db4ounit.fixtures.*;

public class DeferredTest extends Contextful implements TestDecoration {
	
	private final TestFactory _factory;
	private Test _test;

	public DeferredTest(TestFactory factory) {
		_factory = factory;
	}

	public String getLabel() {
		return (String)run(new Closure4() {
			public Object run() {
				return test().getLabel();
			}
		});
	}

	public void run() {
		run(test());
	}
	
	public Test test() {
		if (_test == null) {
			_test = _factory.newInstance();
		}
		return _test;
	}
}
