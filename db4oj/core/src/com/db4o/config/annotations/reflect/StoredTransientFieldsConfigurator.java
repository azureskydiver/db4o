/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations.reflect;

/**
 * @exclude
 * @decaf.ignore
 * @sharpen.ignore
 */
public class StoredTransientFieldsConfigurator extends Db4oConfigurator {
	private String _className;

	private boolean _value;

	public StoredTransientFieldsConfigurator(String name, boolean value_) {
		_className = name;
		_value = value_;
	}

	@Override
	protected void configure() {
		objectClass(_className).storeTransientFields(_value);
	}

}
