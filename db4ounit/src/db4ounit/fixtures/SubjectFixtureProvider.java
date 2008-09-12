/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.fixtures;

import com.db4o.foundation.*;

public class SubjectFixtureProvider implements FixtureProvider {
	
	public static Object value() {
		return _variable.value();
	}
	
	private static final FixtureVariable _variable = new FixtureVariable("subject");
	private final Iterable4 _values;
	
	public SubjectFixtureProvider(Iterable4 values) {
		_values = values;
	}
	
	public SubjectFixtureProvider(Object[] values) {
		this(Iterators.iterable(values));
	}

	public FixtureVariable variable() {
		return _variable;
	}

	public Iterator4 iterator() {
		return _values.iterator();
	}
}
