/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs;

public class FieldMeta {
	private String _fieldName;

	private ClassMeta _fieldClass;
	
	public FieldMeta() {
	}

	public FieldMeta(String fieldName, ClassMeta fieldClass) {
		_fieldName = fieldName;
		_fieldClass = fieldClass;
	}

	public ClassMeta getFieldClass() {
		return _fieldClass;
	}

	public String getFieldName() {
		return _fieldName;
	}
}
