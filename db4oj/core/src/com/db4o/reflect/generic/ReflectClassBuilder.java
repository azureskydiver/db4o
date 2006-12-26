/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.reflect.*;

public interface ReflectClassBuilder {
	ReflectClass createClass(String name,ReflectClass superClass,int fieldCount);
	ReflectField createField(ReflectClass parentType,String fieldName,ReflectClass fieldType,boolean isVirtual,boolean isPrimitive,boolean isArray, boolean isNArray);
	void initFields(ReflectClass clazz,ReflectField[] fields);
	ReflectClass arrayClass(ReflectClass clazz);
	ReflectField[] fieldArray(int length);
}
