/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4odoc.f1.reflections;

import com.db4o.reflect.ReflectArray;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;
import com.db4o.reflect.jdk.JdkClass;

public class LoggingReflector implements Reflector {
	private ReflectArray _array;
	
	private Reflector _parent;


	public LoggingReflector() {
	
	}
	
	public ReflectArray array() {
		 if(_array == null){
	            _array = new LoggingArray(_parent);
	        }
		 return _array;
	}

	public boolean constructorCallsSupported() {
		return true;
	}

	public ReflectClass forClass(Class clazz) {
		ReflectClass rc = new JdkClass(_parent, clazz); 
		System.out.println("forClass: " + clazz+" -> "+(rc== null ? "" : rc.getName()));    
		return rc;
	}

	public ReflectClass forName(String className) {
		try {
			Class clazz = Class.forName(className);
			ReflectClass rc = forClass(clazz);
			System.out.println("forName: " + className+" -> "+(rc== null ? "" : rc.getName()));
			return rc;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public ReflectClass forObject(Object a_object) {
		if (a_object == null) {
			return null;
		}
		ReflectClass rc = _parent.forClass(a_object.getClass()); 
		System.out.println("forObject:" + a_object+" -> "+(rc== null ? "" : rc.getName()));
		return rc;
	}

	public boolean isCollection(ReflectClass claxx) {
		return false;
	}

	public void setParent(Reflector reflector) {
		_parent = reflector;
	}

	public Object deepClone(Object context) {
		return new LoggingReflector();
	}
}
