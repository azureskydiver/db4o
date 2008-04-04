/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.fixtures;

import com.db4o.foundation.*;

public class SimpleFixtureProvider implements FixtureProvider {

	private final Fixture _variable;
	private final Object[] _values;

	public SimpleFixtureProvider(Fixture variable, Object value) {
		this(variable, new Object[] { value });
	}

	public SimpleFixtureProvider(Fixture variable, Object[] values) {
		_variable = variable;
		_values = values;
	}

	public Fixture fixture() {
		return _variable;
	}

	public Iterator4 iterator() {
		return Iterators.iterate(_values);
	}	
}
