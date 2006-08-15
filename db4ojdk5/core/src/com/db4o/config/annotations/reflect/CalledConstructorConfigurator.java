/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations.reflect;

/**
 * @exclude
 */
public class CalledConstructorConfigurator extends Db4oConfigurator {
	private String _className;

	private boolean _value;

	public CalledConstructorConfigurator(String className, boolean value) {
		this._className = className;
		this._value = value;
	}

	@Override
	protected void configure() {
		objectClass(_className).callConstructor(_value);
	}

}
