/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures;

import com.db4o.foundation.*;

import db4ounit.fixtures.*;

public class SimpleFixtureProvider implements FixtureProvider {

	private final ContextVariable _variable;
	private final Object[] _values;

	public SimpleFixtureProvider(ContextVariable variable, Object[] values) {
		_variable = variable;
		_values = values;
	}

	public ContextVariable variable() {
		return _variable;
	}

	public Iterator4 iterator() {
		return Iterators.iterate(_values);
	}

}
