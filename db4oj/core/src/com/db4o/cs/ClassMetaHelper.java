/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs;

import java.lang.reflect.Field;

import com.db4o.foundation.Hashtable4;
import com.db4o.reflect.generic.GenericClass;
import com.db4o.reflect.generic.GenericField;
import com.db4o.reflect.generic.GenericReflector;

public class ClassMetaHelper {
	
	private Hashtable4 _classMetaTable = new Hashtable4();

	private Hashtable4 _genericClassTable = new Hashtable4();

	public ClassMeta getClassMeta(Class claxx) {
		
		String className = claxx.getName();
		if (isSystemClass(className)) {
			return new ClassMeta(className, true);
		}

		// look up from metaClass table.
		ClassMeta classMeta = lookupClassMeta(className);
		if (classMeta != null) {
			return classMeta;
		}

		// set classMeta for user-defined class
		classMeta = new ClassMeta(className, false);
		classMeta.setSuperClass(mapSuperclass(claxx));

		// register classMeta
		registerClassMeta(className, classMeta);

		// set fields
		classMeta.setFields(mapFields(claxx.getDeclaredFields()));
		return classMeta;
	}

	private ClassMeta mapSuperclass(Class claxx) {
		Class superClass = claxx.getSuperclass();
		if (superClass != null && superClass != Object.class) {
			return getClassMeta(superClass);
		}
		return null;
	}

	private FieldMeta[] mapFields(Field[] fields) {
		FieldMeta[] fieldsMeta = new FieldMeta[fields.length];
		for (int i = 0; i < fields.length; ++i) {
			Class fieldClass = fields[i].getType();
			String fieldName = fields[i].getName();
			ClassMeta fieldClassMeta = getClassMeta(fieldClass);
			fieldsMeta[i] = new FieldMeta();
			fieldsMeta[i].setFieldName(fieldName);
			fieldsMeta[i].setFieldClass(fieldClassMeta);
		}
		return fieldsMeta;
	}

	private static boolean isSystemClass(String className) {
		return className.startsWith("java");
	}

	private ClassMeta lookupClassMeta(String className) {
		return (ClassMeta) _classMetaTable.get(className);
	}

	private void registerClassMeta(String className, ClassMeta classMeta) {
		_classMetaTable.put(className, classMeta);
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
		return (GenericClass) _genericClassTable.get(className);
	}

	private void registerGenericClass(String className, GenericClass classMeta) {
		_genericClassTable.put(className, classMeta);
	}

}
