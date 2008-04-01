/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.fixtures;

import com.db4o.foundation.*;

public class SimpleFixtureProvider implements FixtureProvider {

	private final ContextVariable _variable;
	private final Object[] _values;
	private final String _label;

	public SimpleFixtureProvider(ContextVariable variable, Object[] values) {
		this("", variable, values);
	}
	
	public SimpleFixtureProvider(ContextVariable variable, Object value) {
		this(variable, new Object[] { value });
	}
	
	public SimpleFixtureProvider(String label, ContextVariable variable, Object value) {
		this(label, variable, new Object[] { value });
	}

	public SimpleFixtureProvider(String label, ContextVariable variable, Object[] values) {
		_label = label;
		_variable = variable;
		_values = values;
	}

	public ContextVariable variable() {
		return _variable;
	}

	public Iterator4 iterator() {
		return Iterators.iterate(_values);
	}

	public String label() {
		return _label;
	}

}
