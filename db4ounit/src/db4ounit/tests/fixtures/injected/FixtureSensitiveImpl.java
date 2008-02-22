/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures.injected;

public class FixtureSensitiveImpl implements FixtureSensitive {

	private Fixtures _fixtures;

	public FixtureSensitiveImpl() {
		super();
	}

	public void fixtures(Fixtures fixtures) {
		_fixtures = fixtures;
	}

	protected Object fixture(final Object token) {
		return _fixtures.get(token);
	}

}