/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;


public class ContextfulIterator implements Iterator4 {
	
	private final Iterator4 _delegate;
	private final ContextVariable _variable;
	private final Object _value;

	public ContextfulIterator(ContextVariable variable, Iterator4 delegate) {
		_variable = variable;
		_value = _variable.value();
		_delegate = delegate;
	}

	public Object current() {
		return withContext(new Closure4() {
			public Object run() {
				return _delegate.current();
			}
		});	
	}
	
	public boolean moveNext() {
		final BooleanByRef result = new BooleanByRef();
		withContext(new Runnable() {
			public void run() {
				result.value = _delegate.moveNext();
			}
		});
		return result.value;
	}
	
	public void reset() {
		withContext(new Runnable() {
			public void run() {
				_delegate.reset();
			}
		});
	}

	private void withContext(final Runnable block) {
		_variable.with(_value, block);
	}

	private Object withContext(final Closure4 closure) {
		return _variable.with(_value, closure);
	}
}
