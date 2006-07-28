/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations.reflect;

public class GeneratedVersionNumbersConfigurator extends Db4oConfigurator {

	private String _className;

	private boolean _value;

	public GeneratedVersionNumbersConfigurator(String name, boolean _value) {
		_className = name;
		this._value = _value;
	}

	@Override
	protected void configure() {
		objectClass(_className).generateVersionNumbers(_value);
	}

}
