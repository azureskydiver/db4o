package com.db4o.foundation;

/**
 * @exclude
 */
public class KeySpec {
	private final Object defaultValue;
	
	public KeySpec(byte defaultValue) {
		this(new Byte(defaultValue));
	}

	public KeySpec(int defaultValue) {
		this(new Integer(defaultValue));
	}

	public KeySpec(boolean defaultValue) {
		this(new Boolean(defaultValue));
	}

	public KeySpec(Object defaultValue) {
		this.defaultValue=defaultValue;
	}

	public Object defaultValue() {
		return defaultValue;
	}		
}