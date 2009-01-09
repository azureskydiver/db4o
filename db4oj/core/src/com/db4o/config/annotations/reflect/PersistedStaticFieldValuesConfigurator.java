/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations.reflect;

/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore
public class PersistedStaticFieldValuesConfigurator extends Db4oConfigurator {
	private String _className;

	public PersistedStaticFieldValuesConfigurator(String name) {
		_className = name;
	}

	@Override
	protected void configure() {
		objectClass(_className).persistStaticFieldValues();
	}

}
