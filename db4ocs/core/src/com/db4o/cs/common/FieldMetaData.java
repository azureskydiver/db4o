package com.db4o.cs.common;

/**
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 1:05:41 PM
 */
public class FieldMetaData {
	private String fieldName;
	private String className;

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getClassName() {
		return className;
	}
}
