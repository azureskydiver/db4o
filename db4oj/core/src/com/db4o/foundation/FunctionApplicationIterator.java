/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class FunctionApplicationIterator extends MappingIterator {

	private final Function4 _function;
	
	public FunctionApplicationIterator(Iterator4 iterator, Function4 function) {
		super(iterator);
		if (null == function) {
			throw new ArgumentNullException();
		}
		_function = function;
	}
	
	protected Object map(final Object current) {
		return _function.apply(current);
	}	
}
