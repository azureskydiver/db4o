/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.fixtures;

import com.db4o.foundation.*;

import db4ounit.*;

final class FixtureDecorator implements TestDecorator {
	private final Object _fixture;
	private final ContextVariable _variable;

	FixtureDecorator(ContextVariable variable, Object fixture) {
		_fixture = fixture;
		_variable = variable;
	}

	public Test decorate(Test test) {
		return new FixtureDecoration(test, _variable, _fixture);
	}
}