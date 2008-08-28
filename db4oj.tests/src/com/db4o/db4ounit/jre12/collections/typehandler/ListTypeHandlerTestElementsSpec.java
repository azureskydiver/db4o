/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.typehandler;

import db4ounit.fixtures.*;


/**
 * @decaf.ignore.jdk11
 */
public class ListTypeHandlerTestElementsSpec implements Labeled {

	public final Object[] _elements;
	public final Object _notContained;
	public final Object _largeElement;
	
	public ListTypeHandlerTestElementsSpec(Object[] elements, Object notContained, Object largeElement) {
		_elements = elements;
		_notContained = notContained;
		_largeElement = largeElement;
	}

	public String label() {
		return getSimpleName(_elements[0].getClass());
	}
	
	private String getSimpleName(Class clazz) {
		String fullName = clazz.getName();
		int lastDotIdx = fullName.lastIndexOf('.');
		if(lastDotIdx < 0) {
			return fullName;
		}
		return fullName.substring(lastDotIdx + 1);
	}
}
