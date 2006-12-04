/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs;

public class FieldMeta {

	public String _fieldName;

	public ClassMeta _fieldClass;

	public boolean _isPrimitive;

	public boolean _isArray;

	public boolean _isNArray;

	public FieldMeta() {
	}

	public FieldMeta(String fieldName, ClassMeta fieldClass,
			boolean isPrimitive, boolean isArray, boolean isNArray) {
		_fieldName = fieldName;
		_fieldClass = fieldClass;
		_isPrimitive = isPrimitive;
		_isArray = isArray;
		_isNArray = isNArray;
	}

	public ClassMeta getFieldClass() {
		return _fieldClass;
	}

	public String getFieldName() {
		return _fieldName;
	}
}
