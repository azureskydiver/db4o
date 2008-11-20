/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.fixtures;

import db4ounit.*;

final class FixtureDecorator implements TestDecorator {
	private final Object _fixture;
	private final FixtureVariable _provider;
	private final int _fixtureIndex;

	FixtureDecorator(FixtureVariable provider, Object fixture, int fixtureIndex) {
		_fixture = fixture;
		_provider = provider;
		_fixtureIndex = fixtureIndex;
	}

	public Test decorate(Test test) {
		String label = _provider.label() + "[" + _fixtureIndex + "]";
		if(_fixture instanceof Labeled) {
			label += ":" + ((Labeled)_fixture).label();
		}
		return new TestWithFixture(test, label, _provider, _fixture);
	}
}