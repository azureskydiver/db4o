package com.db4o.reflect.db;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.io.*;
import com.db4o.reflect.*;

public class DBReflector implements Reflector {

	private Reflector delegate;
	private ObjectContainer db;
	
	public ReflectArray array() {
		return new DBArray();
	}

	public boolean constructorCallsSupported() {
		return false;
	}

	public ReflectClass forName(String className) {
		if(delegate!=null) {
			ReflectClass clazz=delegate.forName(className);
			if(clazz!=null) {
				return clazz;
			}
		}
		return createClass(className);
	}

	public ReflectClass forClass(Class clazz) {
		if(db==null) {
			return delegate.forClass(clazz);
		}
		return createClass(clazz.getName());
	}

	public ReflectClass forObject(Object a_object) {
		if(db==null) {
			return delegate.forObject(a_object);
		}
		return createClass(a_object.getClass().getName());
	}

	private ReflectClass createClass(String name) {
		ClassReader storedClass=db.ext().leanStoredClassByName(name);
		return (storedClass==null ? null : new DBClass(this, db.ext(),storedClass));
	}
	
	public boolean isCollection(ReflectClass claxx) {
		return false;
	}

	public void registerCollection(Class clazz) {
	}

	public void registerCollectionUpdateDepth(Class clazz, int depth) {
	}

	public int collectionUpdateDepth(ReflectClass claxx) {
		return 0;
	}

	public void setDatabase(ObjectContainer db) {
		this.db=db;
	}

	public void setDelegate(Reflector delegate) {
		this.delegate=delegate;
	}
}
