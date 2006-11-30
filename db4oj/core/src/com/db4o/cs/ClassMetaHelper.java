/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.db4o.reflect.generic.GenericClass;
import com.db4o.reflect.generic.GenericField;
import com.db4o.reflect.generic.GenericReflector;

public class ClassMetaHelper {
	
	private Map classMetaTable = new HashMap();

	private Map genericClassTable = new HashMap();

	public ClassMeta getClassMeta(Class claxx) {
		ClassMeta classMeta = null;
		String className = claxx.getName();

		if (isSystemClass(className)) {
			classMeta = new ClassMeta();
			classMeta.setClassName(className);
			classMeta.setSystemClass(true);
			return classMeta;
		}

		// look up from metaClass table.
		classMeta = lookupClassMeta(className);
		if (classMeta != null) {
			return classMeta;
		}

		// get ClassMeta for superclass
		ClassMeta superClassMeta = null;
		Class superClass = claxx.getSuperclass();
		if (superClass != null && superClass != Object.class) {
			superClassMeta = getClassMeta(superClass);
		}

		// set classMeta for user-defined class
		classMeta = new ClassMeta();
		classMeta.setSuperClass(superClassMeta);
		classMeta.setClassName(className);
		classMeta.setSystemClass(false);

		// register classMeta
		registerClassMeta(className, classMeta);

		// set fields
		Field[] fields = claxx.getDeclaredFields();
		FieldMeta[] fieldsMeta = new FieldMeta[fields.length];
		for (int i = 0; i < fields.length; ++i) {
			Class fieldClass = fields[i].getType();
			String fieldName = fields[i].getName();
			ClassMeta fieldClassMeta = getClassMeta(fieldClass);
			fieldsMeta[i] = new FieldMeta();
			fieldsMeta[i].setFieldName(fieldName);
			fieldsMeta[i].setFieldClass(fieldClassMeta);
		}

		classMeta.setFields(fieldsMeta);
		return classMeta;
	}

	private static boolean isSystemClass(String className) {
		return className.startsWith("java");
	}

	private ClassMeta lookupClassMeta(String className) {
		return (ClassMeta) classMetaTable.get(className);
	}

	private void registerClassMeta(String className, ClassMeta classMeta) {
		classMetaTable.put(className, classMeta);
	}

	public GenericClass classMetaToGenericClass(GenericReflector reflector,
			ClassMeta classMeta) {
		if (classMeta.isSystemClass()) {
			return (GenericClass) reflector.forName(classMeta.getClassName());
		}

		String className = classMeta.getClassName();
		// look up from generic class table.
		GenericClass genericClass = lookupGenericClass(className);
		if (genericClass != null) {
			return genericClass;
		}

		GenericClass genericSuperClass = null;
		ClassMeta superClassMeta = classMeta.getSuperClass();
		if (superClassMeta != null) {
			genericSuperClass = classMetaToGenericClass(reflector,
					superClassMeta);
			registerGenericClass(superClassMeta.getClassName(),
					genericSuperClass);
		}

		genericClass = new GenericClass(reflector, null,className, genericSuperClass);
		registerGenericClass(className, genericClass);
		
		FieldMeta[] fields = classMeta.getFields();
		GenericField[] genericFields = new GenericField[fields.length];

		for (int i = 0; i < fields.length; ++i) {
			ClassMeta fieldClassMeta = fields[i].getFieldClass();
			String fieldName = fields[i].getFieldName();
			GenericClass genericFieldClass = classMetaToGenericClass(reflector,
					fieldClassMeta);
			// TODO: needs to handle Array, NArray
			genericFields[i] = new GenericField(fieldName, genericFieldClass,
					false, false, false);
		}
		
		genericClass.initFields(genericFields);
		return genericClass;
	}

	private GenericClass lookupGenericClass(String className) {
		return (GenericClass) genericClassTable.get(className);
	}

	private void registerGenericClass(String className, GenericClass classMeta) {
		genericClassTable.put(className, classMeta);
	}

}
