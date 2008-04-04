/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.fixtures;

import com.db4o.foundation.*;

/**
 * Set of live {@link Fixture}/value pairs.
 * 
 */
public class FixtureContext {
	
	private static final ContextVariable _current = new ContextVariable() {
		private final FixtureContext EMPTY_CONTEXT = new FixtureContext();
		protected Object defaultValue() {
			return EMPTY_CONTEXT;
		}
	};
	
	public static FixtureContext current() {
		return (FixtureContext)_current.value();
	}
	
	public FixtureContext combine(final FixtureContext parent) {
		return new FixtureContext() {
			public Found get(Fixture fixture) {
				Found found = FixtureContext.this.get(fixture);
				if (null != found) return found;
				return parent.get(fixture);
			}
		};
	}

	public Found get(Fixture fixture) {
		return null;
	}
	
	public Object run(Closure4 closure) {
		return _current.with(this, closure);
	}

	public void run(Runnable block) {
		_current.with(this, block);
	}
	
	public static class Found {
		public final Object value;
		
		public Found(Object value_) {
			value = value_;
		}
	}

	FixtureContext add(final Fixture fixture, final Object value) {
		return new FixtureContext() {
			public Found get(Fixture key) {
				if (key == fixture) {
					return new Found(value);
				}
				return FixtureContext.this.get(key);
			}
		};
	}
}
