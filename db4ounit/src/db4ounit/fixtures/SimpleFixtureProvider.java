/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package db4ounit.fixtures;

import com.db4o.foundation.*;

public class SimpleFixtureProvider implements FixtureProvider {

	private final FixtureVariable _variable;
	private final Object[] _values;

	public <T> SimpleFixtureProvider(FixtureVariable variable, T... values) {
		_variable = variable;
		_values = values;
	}

	public FixtureVariable variable() {
		return _variable;
	}

	public Iterator4 iterator() {
		return Iterators.iterate(_values);
	}	
}
