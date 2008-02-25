/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.fixtures;

import db4ounit.*;

final class FixtureDecorator implements TestDecorator {
	private final Object _fixture;
	private final FixtureProvider _provider;
	private final int _fixtureIndex;

	FixtureDecorator(FixtureProvider provider, Object fixture, int fixtureIndex) {
		_fixture = fixture;
		_provider = provider;
		_fixtureIndex = fixtureIndex;
	}

	public Test decorate(Test test) {
		return new FixtureDecoration(test, _provider.label() + "[" + _fixtureIndex + "]", _provider.variable(), _fixture);
	}
}