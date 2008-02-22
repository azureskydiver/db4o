/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures.dynamic;

import com.db4o.foundation.*;

public class MultiValueFixtureProvider implements FixtureProvider {

	public static Object[] value() {
		return (Object[])_variable.value();
	}
	
	private static final ContextVariable _variable = new ContextVariable();
	
	private final Object[][] _values;

	public MultiValueFixtureProvider(Object[][] values) {
		_values = values;
	}

	public ContextVariable variable() {
		return _variable;
	}

	public Iterator4 iterator() {
		return Iterators.iterate(_values);
	}

}
