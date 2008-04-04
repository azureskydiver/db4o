/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.fixtures;

import com.db4o.foundation.*;

public class SimpleFixtureProvider implements FixtureProvider {

	private final FixtureVariable _variable;
	private final Object[] _values;

	public SimpleFixtureProvider(FixtureVariable variable, Object value) {
		this(variable, new Object[] { value });
	}

	public SimpleFixtureProvider(FixtureVariable variable, Object[] values) {
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
