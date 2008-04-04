/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.fixtures;

import com.db4o.foundation.*;

import db4ounit.fixtures.FixtureContext.*;

public class FixtureVariable {
	
	private final String _label;
	
	public FixtureVariable() {
		this("");
	}

	public FixtureVariable(String label) {
		_label = label;
	}
	
	/**
	 * @sharpen.property
	 */
	public String label() {
		return _label;
	}
	
	public String toString() {
		return _label;
	}
	
	public Object with(Object value, Closure4 closure) {
		return inject(value).run(closure);
	}

	public void with(Object value, Runnable runnable) {
		inject(value).run(runnable);
	}

	private FixtureContext inject(Object value) {
		return currentContext().add(this, value);
	} 
	
	/**
	 * @sharpen.property
	 */
	public Object value() {
		final Found found = currentContext().get(this);
		if (null == found) throw new IllegalStateException();
		return found.value;
	}

	private FixtureContext currentContext() {
		return FixtureContext.current();
	}
}
