/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import db4ounit.fixtures.*;

public class ArrayListHandlerTestElementsSpec implements Labeled {

	public final Object[] _elements;
	public final Object _notContained;
	
	public ArrayListHandlerTestElementsSpec(Object[] elements, Object notContained) {
		_elements = elements;
		_notContained = notContained;
	}

	public String label() {
		return _elements[0].getClass().getSimpleName();
	}
}
