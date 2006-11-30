/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs;

public class ClassMeta {
	private String className;
	private boolean isSystemClass;
	private ClassMeta superClass;
	private FieldMeta [] fields;
	
	public FieldMeta[] getFields() {
		return fields;
	}
	public void setFields(FieldMeta[] fields) {
		this.fields = fields;
	}
	public ClassMeta getSuperClass() {
		return superClass;
	}
	public void setSuperClass(ClassMeta superClass) {
		this.superClass = superClass;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public void setSystemClass(boolean isSystemClass) {
		this.isSystemClass = isSystemClass;
	}
	public boolean isSystemClass() {
		return isSystemClass;
	}
	
}
