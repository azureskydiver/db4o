/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit;

import com.db4o.foundation.*;

import db4ounit.fixtures.*;

public class ContextfulTest extends Contextful implements Test {
	
	private final TestFactory _factory;

	public ContextfulTest(TestFactory factory) {
		_factory = factory;
	}

	public String label() {
		return (String)run(new Closure4() {
			public Object run() {
				return testInstance().label();
			}
		});
	}

	public void run() {
		run(testInstance());
	}

	private Test testInstance() {
		return _factory.newInstance();
	}
}
