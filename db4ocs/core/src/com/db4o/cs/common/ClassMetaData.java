package com.db4o.cs.common;

import com.db4o.cs.common.FieldMetaData;

import java.util.List;
import java.util.ArrayList;

/**
 * todo: move to /common
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 1:04:24 PM
 */
public class ClassMetaData {
	private String className;
	private List<FieldMetaData> fields = new ArrayList();
	private transient List reflectionFields;

	public void setClassName(String className) {
		this.className = className;
	}

	public void addField(FieldMetaData fmd) {
		fields.add(fmd);
	}

	public String getClassName() {
		return className;
	}

	public int getFieldCount() {
		return fields.size();
	}

	public List getFields() {
		return fields;
	}

	public void setReflectionFields(List reflectFields) {
		this.reflectionFields = reflectFields;

	}

	public List getReflectionFields() {
		return reflectionFields;
	}
}
