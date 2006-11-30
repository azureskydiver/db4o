/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs;

public class FieldMeta {
	private String fieldName;
	private ClassMeta fieldClass;
	public ClassMeta getFieldClass() {
		return fieldClass;
	}
	public void setFieldClass(ClassMeta fieldClass) {
		this.fieldClass = fieldClass;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
