/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations.reflect;

import com.db4o.config.*;

public abstract class FieldConfigurator extends Db4oConfigurator {
	private String _className;
	private String _fieldName;

	public FieldConfigurator(String className, String fieldName) {
		super();
		_className = className;
		_fieldName = fieldName;
	}
	
	@Override
	protected void configure() {
		configure(objectClass(_className).objectField(_fieldName));
	}
	
	protected abstract void configure(ObjectField objectField);
}
