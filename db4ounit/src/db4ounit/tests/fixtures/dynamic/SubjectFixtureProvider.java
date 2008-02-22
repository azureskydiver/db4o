/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures.dynamic;

import com.db4o.foundation.*;

public class SubjectFixtureProvider implements FixtureProvider {
	
	public static Object value() {
		return _variable.value();
	}
	
	private static final ContextVariable _variable = new ContextVariable();
	private final Iterable4 _values;
	
	public SubjectFixtureProvider(Iterable4 values) {
		_values = values;
	}
	
	public SubjectFixtureProvider(Object[] values) {
		_values = Iterators.iterable(values);
	}

	public ContextVariable variable() {
		return _variable;
	}

	public Iterator4 iterator() {
		return _values.iterator();
	}
}
