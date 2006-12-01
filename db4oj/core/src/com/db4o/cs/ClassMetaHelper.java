/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs;

import com.db4o.foundation.Hashtable4;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.generic.GenericClass;
import com.db4o.reflect.generic.GenericField;
import com.db4o.reflect.generic.GenericReflector;

public class ClassMetaHelper {

	private Hashtable4 _classMetaTable = new Hashtable4();

	private Hashtable4 _genericClassTable = new Hashtable4();

	public ClassMeta getClassMeta(ReflectClass claxx) {

		String className = claxx.getName();
		if (isSystemClass(className)) {
			return ClassMeta.newSystemClass(className);
		}

		ClassMeta existing = lookupClassMeta(className);
		if (existing != null) {
			return existing;
		}

		return newUserClassMeta(claxx);
	}

	private ClassMeta newUserClassMeta(ReflectClass claxx) {

		ClassMeta classMeta = ClassMeta.newUserClass(claxx.getName());
		classMeta.setSuperClass(mapSuperclass(claxx));

		registerClassMeta(claxx.getName(), classMeta);

		classMeta.setFields(mapFields(claxx.getDeclaredFields()));
		return classMeta;
	}

	private ClassMeta mapSuperclass(ReflectClass claxx) {
		ReflectClass superClass = claxx.getSuperclass();
		if (superClass != null) {
			return getClassMeta(superClass);
		}
		return null;
	}

	private FieldMeta[] mapFields(ReflectField[] fields) {
		FieldMeta[] fieldsMeta = new FieldMeta[fields.length];
		for (int i = 0; i < fields.length; ++i) {
			final ReflectField field = fields[i];
			fieldsMeta[i] = new FieldMeta(field.getName(), getClassMeta(field
					.getFieldType()));
		}
		return fieldsMeta;
	}

	private static boolean isSystemClass(String className) {
		// TODO: We should send the whole class meta if we'd like to support
		// java and .net communication (We have this request in our user forum
		// http://developer.db4o.com/forums/thread/31504.aspx). If we only want
		// to support java & .net platform separately, then this method should
		// be moved to Platform4.
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

		genericClass = new GenericClass(reflector, null, className,
				genericSuperClass);
		registerGenericClass(className, genericClass);

		FieldMeta[] fields = classMeta.getFields();
		GenericField[] genericFields = new GenericField[fields.length];

		for (int i = 0; i < fields.length; ++i) {
			ClassMeta fieldClassMeta = fields[i].getFieldClass();
			String fieldName = fields[i].getFieldName();
			GenericClass genericFieldClass = classMetaToGenericClass(reflector,
					fieldClassMeta);
			// TODO: needs to handle primitive, Array, NArray
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
