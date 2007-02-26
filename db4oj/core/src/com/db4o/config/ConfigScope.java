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

	private static final String DISABLED_NAME="disabled";
	private static final String INDIVIDUALLY_NAME="individually";
	private static final String GLOBALLY_NAME="globally";
	
	/**
	 * Marks a configuration feature as globally disabled.
	 */
	public static final ConfigScope DISABLED = new ConfigScope(DISABLED_ID,DISABLED_NAME);

	/**
	 * Marks a configuration feature as individually configurable.
	 */
	public static final ConfigScope INDIVIDUALLY = new ConfigScope(INDIVIDUALLY_ID,INDIVIDUALLY_NAME);

	/**
	 * Marks a configuration feature as globally enabled.
	 */
	public static final ConfigScope GLOBALLY = new ConfigScope(GLOBALLY_ID,GLOBALLY_NAME);

	private final int _value;
	private final String _name;
	
	private ConfigScope(int value,String name) {
		_value=value;
		_name=name;
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
		}
		return GLOBALLY;
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
	
	public String toString() {
		return _name;
	}
}
