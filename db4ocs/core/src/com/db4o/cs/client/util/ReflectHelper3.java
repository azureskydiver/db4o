package com.db4o.cs.client.util;

import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

import java.util.*;
import java.lang.reflect.Field;

/**
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 12:13:45 PM
 */
public class ReflectHelper3 {
	// todo: getDeclaredFieldsInHeirarchy should exclude overridden fields in super classes		
	public static List<Field> getDeclaredFieldsInHeirarchy(Class aClass) {
		List ret = getDeclaredFields(aClass);
		Class parent = aClass.getSuperclass();
		if (parent != null) {
			ret.addAll(getDeclaredFieldsInHeirarchy(parent));
		}
		return ret;
	}

	public static List<Field> getDeclaredFields(Class aClass) {
		List<Field> ret = new ArrayList();
		Field[] fields = aClass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			ret.add(field);
		}
		return ret;
	}

	public static Field getDeclaredFieldInHeirarchy(Class reflectClass, String field) throws NoSuchFieldException {
		Field rf = reflectClass.getDeclaredField(field);
		if (rf == null) {
			// check up heirarchy
			Class parent = reflectClass.getSuperclass();
			if (parent != null) {
				return getDeclaredFieldInHeirarchy(parent, field);
			}
		}
		return rf;
	}
	 /**
     * <p>
     * Equivalent to isLeaf, isEditable, isSortaPrimitive... ;)
     * </p>
     * @param c
     * @return
     */
    public static boolean isSecondClass(Class c) {
		return c.isPrimitive()
				|| String.class.isAssignableFrom(c)
				|| Number.class.isAssignableFrom(c)
				|| Date.class.isAssignableFrom(c)
				|| Boolean.class.isAssignableFrom(c)
				|| Character.class.isAssignableFrom(c);
	}

	public static boolean isCollection(Class c) {
		return Collection.class.isAssignableFrom(c);
	}

	public static boolean isMap(Class c) {
		return Map.class.isAssignableFrom(c);
	}
}
