/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations.reflect;

/**
 * @exclude
 * @decaf.ignore
 */
public class GeneratedVersionNumbersConfigurator extends Db4oConfigurator {

	private String _className;

	private boolean _value;

	public GeneratedVersionNumbersConfigurator(String name, boolean value_) {
		_className = name;
		_value = value_;
	}

	@Override
	protected void configure() {
		objectClass(_className).generateVersionNumbers(_value);
	}

}
