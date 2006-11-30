/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs;

public class FieldMeta {
	private String _fieldName;

	private ClassMeta _fieldClass;

	public ClassMeta getFieldClass() {
		return _fieldClass;
	}

	public void setFieldClass(ClassMeta fieldClass) {
		this._fieldClass = fieldClass;
	}

	public String getFieldName() {
		return _fieldName;
	}

	public void setFieldName(String fieldName) {
		this._fieldName = fieldName;
	}
}
