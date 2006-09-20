package com.db4o.test.traversal;

import java.util.Vector;

class TraversalTestSubject {

	Object _null = null;
	Object _simple = new Object();

	static Object _static = new Object(); //Must NOT be visited.
	transient Object _transient = new Object(); //Must NOT be visited.

	String _string = "Hi";
	int _int = 42;
	Integer _integer = new Integer(42);

	Object[] _array = new Object[] {new Object(), null, new Object()};
	Object[][][] _multiDimArray = new Object[][][] {{{new Object(), new Object(), new Object()},{new Object()}}, {{new Object()},{new Object()}},   {{new Object()},{new Object()}}, {{new Object()},{}}};

	Vector _vector = new Vector();
	{
		_vector.add(new Object());
		_vector.add(new Object());
		_vector.add(this);
	}

	int objectsReferenced() {
		return 14;
	}

	String fields() {
		return "_null,_simple,_string,_int,_integer,_array,_multiDimArray,_vector,";
	}
}
