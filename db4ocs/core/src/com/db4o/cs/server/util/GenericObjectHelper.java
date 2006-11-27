package com.db4o.cs.server.util;

import com.db4o.cs.common.ClassMetaData;
import com.db4o.cs.common.FieldMetaData;
import com.db4o.reflect.generic.GenericClass;
import com.db4o.reflect.generic.GenericReflector;
import com.db4o.reflect.generic.GenericField;
import com.db4o.reflect.jdk.JdkReflector;
import com.db4o.reflect.ReflectClass;

import java.util.List;

/**
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 1:16:23 PM
 */
public class GenericObjectHelper {
	public static ReflectClass createGenericClass(ClassMetaData classMetaData) {
		GenericReflector reflector = new GenericReflector(null, new JdkReflector(Thread.currentThread().getContextClassLoader()));
		GenericClass genericClass = initGenericClass(reflector, classMetaData.getClassName());
		genericClass.initFields(fields(reflector, classMetaData.getFields()));
		return genericClass;
	}

	private static GenericClass initGenericClass(GenericReflector reflector, String className) {
		GenericClass _objectIClass = (GenericClass) reflector.forClass(Object.class);
		GenericClass result = new GenericClass(reflector, null,
				className, _objectIClass);

		return result;
	}

	private static GenericField[] fields(GenericReflector reflector, List fields) {
		GenericField[] gFields = new GenericField[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			FieldMetaData fieldMetaData = (FieldMetaData) fields.get(i);
			gFields[i] = new GenericField(fieldMetaData.getFieldName(), reflector.forName(fieldMetaData.getClassName()),
					false, false, false);
		}
		return gFields;
	}

}
