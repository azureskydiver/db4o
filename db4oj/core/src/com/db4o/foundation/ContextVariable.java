/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * A context variable is a value associated to a specific thread and scope.
 * 
 * The value is brought into scope with the {@link #with} method.
 * 
 */
public class ContextVariable {
	
	private static class ThreadSlot {
		public final Thread thread;
		public final Object value;
		public ThreadSlot next;
		
		public ThreadSlot(Object value_, ThreadSlot next_) {
			thread = Thread.currentThread();
			value = value_;
			next = next_;
		}
	}
	
	private final Class _expectedType;
	private ThreadSlot _values = null;
	
	public ContextVariable() {
		this(null);
	}
	
	public ContextVariable(Class expectedType) {
		_expectedType = expectedType;
	}
	
	public Object value() {
		final Thread current = Thread.currentThread();
		synchronized (this) {
			ThreadSlot slot = _values;
			while (null != slot) {
				if (slot.thread == current) {
					return slot.value;
				}
			}
		}
		return null;
	}
	
	public Object with(Object value, SafeClosure4 block) {
		validate(value);
		
		ThreadSlot slot = pushValue(value);
		try {
			return block.run();
		} finally {
			popValue(slot);
		}
	}
	
	public void with(Object value, final Runnable block) {
		with(value, new SafeClosure4() {
			public Object run() {
				block.run();
				return null;
			}
		});
	}

	private void validate(Object value) {
		if (value == null || _expectedType == null) {
			return;
		}
		if (_expectedType.isInstance(value)) {
			return;
		}
		throw new IllegalArgumentException("Expecting instance of '" + _expectedType + "' but got '" + value + "'");
	}

	private synchronized void popValue(ThreadSlot slot) {
		if (slot == _values) {
			_values = _values.next;
			return;
		}
		
		ThreadSlot previous = _values;
		ThreadSlot current = _values.next;
		while (current != null) {
			if (current == slot) {
				previous.next = current.next;
				return;
			}
		}
	}

	private synchronized ThreadSlot pushValue(Object value) {
		final ThreadSlot slot = new ThreadSlot(value, _values);
		_values = slot;
		return slot;
	}
}
