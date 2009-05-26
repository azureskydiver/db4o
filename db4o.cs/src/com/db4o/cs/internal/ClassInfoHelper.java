/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal;

import com.db4o.foundation.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;

public class ClassInfoHelper {

	private Hashtable4 _classMetaTable = new Hashtable4();

	private Hashtable4 _genericClassTable = new Hashtable4();

	public ClassInfo getClassMeta(ReflectClass claxx) {

		String className = claxx.getName();
		if (isSystemClass(className)) {
			return ClassInfo.newSystemClass(className);
		}

		ClassInfo existing = lookupClassMeta(className);
		if (existing != null) {
			return existing;
		}

		return newUserClassMeta(claxx);
	}

	private ClassInfo newUserClassMeta(ReflectClass claxx) {

		ClassInfo classMeta = ClassInfo.newUserClass(claxx.getName());
		classMeta.setSuperClass(mapSuperclass(claxx));

		registerClassMeta(claxx.getName(), classMeta);

		classMeta.setFields(mapFields(claxx.getDeclaredFields()));
		return classMeta;
	}

	private ClassInfo mapSuperclass(ReflectClass claxx) {
		ReflectClass superClass = claxx.getSuperclass();
		if (superClass != null) {
			return getClassMeta(superClass);
		}
		return null;
	}

	private FieldInfo[] mapFields(ReflectField[] fields) {
		FieldInfo[] fieldsMeta = new FieldInfo[fields.length];
		for (int i = 0; i < fields.length; ++i) {
			final ReflectField field = fields[i];
			boolean isArray = field.getFieldType().isArray();
			ReflectClass fieldClass = isArray ? field.getFieldType().getComponentType() : field.getFieldType();
			boolean isPrimitive = fieldClass.isPrimitive();
			// TODO: need to handle NArray, currently it ignores NArray and alway sets NArray flag false.
			fieldsMeta[i] = new FieldInfo(field.getName(), getClassMeta(fieldClass), isPrimitive, isArray, false);
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

	private ClassInfo lookupClassMeta(String className) {
		return (ClassInfo) _classMetaTable.get(className);
	}

	private void registerClassMeta(String className, ClassInfo classMeta) {
		_classMetaTable.put(className, classMeta);
	}

	public GenericClass classMetaToGenericClass(GenericReflector reflector,
			ClassInfo classMeta) {
		if (classMeta.isSystemClass()) {
			return (GenericClass) reflector.forName(classMeta.getClassName());
		}

		String className = classMeta.getClassName();
		// look up from generic class table.
		GenericClass genericClass = lookupGenericClass(className);
		if (genericClass != null) {
			return genericClass;
		}

		ReflectClass reflectClass = reflector.forName(className);
		if(reflectClass != null) {
			return (GenericClass) reflectClass;
		}
		
		GenericClass genericSuperClass = null;
		ClassInfo superClassMeta = classMeta.getSuperClass();
		if (superClassMeta != null) {
			genericSuperClass = classMetaToGenericClass(reflector,
					superClassMeta);
		}

		genericClass = new GenericClass(reflector, null, className,
				genericSuperClass);
		registerGenericClass(className, genericClass);

		FieldInfo[] fields = classMeta.getFields();
		GenericField[] genericFields = new GenericField[fields.length];

		for (int i = 0; i < fields.length; ++i) {
			ClassInfo fieldClassMeta = fields[i].getFieldClass();
			String fieldName = fields[i].getFieldName();
			GenericClass genericFieldClass = classMetaToGenericClass(reflector,
					fieldClassMeta);
			genericFields[i] = new GenericField(fieldName, genericFieldClass,
					fields[i]._isPrimitive);
		}

		genericClass.initFields(genericFields);
		return genericClass;
	}

	private GenericClass lookupGenericClass(String className) {
		return (GenericClass) _genericClassTable.get(className);
	}

	private void registerGenericClass(String className, GenericClass classMeta) {
		_genericClassTable.put(className, classMeta);
		((GenericReflector)classMeta.reflector()).register(classMeta);
	}

}
