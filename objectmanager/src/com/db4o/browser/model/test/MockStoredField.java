package com.db4o.browser.model.test;

import java.lang.reflect.*;

import com.db4o.ext.*;
import com.db4o.reflect.*;

public class MockStoredField implements StoredField {
	private Field field;
	
	public MockStoredField(Field field) {
		this.field=field;
		field.setAccessible(true);
	}

	public Object get(Object onObject) {
		try {
			return field.get(onObject);
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}

	public String getName() {
		return field.getName();
	}

	public ReflectClass getStoredType() {
		throw new UnsupportedOperationException("TODO: implement");
	}

	public boolean isArray() {
		return field.getType().isArray();
	}

	public void rename(String name) {
		throw new UnsupportedOperationException("TODO: implement");
	}
	
	public String toString() {
		return field.toString();
	}
	
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		return field.equals(((MockStoredField)obj).field);
	}
	
	public int hashCode() {
		return field.hashCode();
	}
}
