package com.db4o.cs.client.batch;

/**
 * User: treeder
 * Date: Dec 11, 2006
 * Time: 5:38:47 PM
 */
public class FieldValue {
	private String fieldName;
	private Object value;


	public FieldValue() {
	}

	public FieldValue(String fieldName, Object value) {
		this.fieldName = fieldName;
		this.value = value;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
