package com.db4o.browser.model.test;

import java.lang.reflect.*;
import java.util.*;

import com.db4o.ext.*;

public class MockStoredClass implements StoredClass {
	private Class clazz;
	
	public MockStoredClass(Class clazz) {
		this.clazz=clazz;
	}

	public String getName() {
		return clazz.getName();
	}

	public long[] getIDs() {
		throw new UnsupportedOperationException("TODO: implement");
	}

	public StoredClass getParentStoredClass() {
		return (clazz.getSuperclass()==null ? null : new MockStoredClass(clazz.getSuperclass()));
	}

	public StoredField[] getStoredFields() {
		List fields=new ArrayList();
		Class curclazz=clazz;
		while(curclazz!=null) {
			Field[] curfields=curclazz.getDeclaredFields();
			for (int idx = 0; idx < curfields.length; idx++) {
				fields.add(new MockStoredField(curfields[idx]));
			}
			curclazz=curclazz.getSuperclass();
		}
		return (StoredField[])fields.toArray(new StoredField[fields.size()]);
	}

	public void rename(String name) {
		throw new UnsupportedOperationException("TODO: implement");
	}

	public StoredField storedField(String name, Object type) {
		throw new UnsupportedOperationException("TODO: implement");
	}
	
	public String toString() {
		return clazz.toString();
	}
	
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		return clazz.equals(((MockStoredClass)obj).clazz);
	}
	
	public int hashCode() {
		return clazz.hashCode();
	}
}
