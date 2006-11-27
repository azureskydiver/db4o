package com.db4o.cs.server;

import com.db4o.cs.common.ClassMetaData;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

import java.util.List;
import java.util.ArrayList;

/**
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 6:56:03 PM
 */
public class ClassMetaDataServer extends ClassMetaData {
	private transient ReflectClass reflectClass;
	private transient List<ReflectField> reflectFields = new ArrayList<ReflectField>();

	public void setReflectClass(ReflectClass reflectClass) {
		this.reflectClass = reflectClass;
	}

	public ReflectClass getReflectClass() {
		return reflectClass;
	}

	public void addReflectField(ReflectField reflectField) {
		reflectFields.add(reflectField);
	}

	public List<ReflectField> getReflectFields() {
		return reflectFields;
	}

	public void setReflectFields(List<ReflectField> reflectFields) {
		this.reflectFields = reflectFields;
	}
}
