/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.config;

import java.io.*;

/**
 * yes/no/dontknow data type
 */
public final class ConfigScope implements Serializable {

	public static final int DISABLED_ID = -1;
	public static final int INDIVIDUALLY_ID = 1;
	public static final int GLOBALLY_ID = Integer.MAX_VALUE;

	/**
	 * Marks a configuration feature as globally disabled.
	 */
	public static final ConfigScope DISABLED = new ConfigScope(DISABLED_ID);

	/**
	 * Marks a configuration feature as individually configurable.
	 */
	public static final ConfigScope INDIVIDUALLY = new ConfigScope(INDIVIDUALLY_ID);

	/**
	 * Marks a configuration feature as globally enabled.
	 */
	public static final ConfigScope GLOBALLY = new ConfigScope(GLOBALLY_ID);

	private final int _value;
	
	private ConfigScope(int value) {
		_value=value;
	}

	public boolean applyConfig(boolean defaultValue) {
		switch(_value) {
			case DISABLED_ID:
				return false;
			case GLOBALLY_ID: 
				return true;
			default:
				return defaultValue;
		}
	}

	/**
	 * @deprecated
	 */
	public static ConfigScope forID(int id) {
		switch(id) {
			case DISABLED_ID:
				return DISABLED;
			case INDIVIDUALLY_ID:
				return INDIVIDUALLY;
			case GLOBALLY_ID:
			default:
				return GLOBALLY;
		}
	}
	
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		ConfigScope tb=(ConfigScope)obj;
		return _value==tb._value;
	}
	
	public int hashCode() {
		return _value;
	}
	
	private Object readResolve() {
		switch(_value) {
		case DISABLED_ID:
			return DISABLED;
		case INDIVIDUALLY_ID:
			return INDIVIDUALLY;
		default:
			return GLOBALLY;
		}
	}
}
