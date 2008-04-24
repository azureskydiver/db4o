/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;


/**
 * @sharpen.ignore
 */
public class ReflectPlatform {

	/**
	 * @param className
	 * @return the Class object for specified className. Returns null if an
	 *         error is encountered during loading the class.
	 */
	public static Class forName(String className) {
		try {
			return Class.forName(className);
		} catch (Exception e) {
			// e.printStackTrace();
		} catch (LinkageError e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static Object createInstance(String className) {
		Class clazz = forName(className);
		return createInstance(clazz);
	}

	public static Object createInstance(Class clazz) {
		if (clazz == null) {
			return null;
		}
		try {
			return clazz.newInstance();
		} catch (Throwable t) {
			// Class.newInstances() propagates any exception thrown by the
			// nullary constructor, including a checked exception.
		}
		return null;
	}

	public static String fullyQualifiedName(Class clazz) {
		return clazz.getName();
	}

	public static boolean isNamedClass(Class clazz) {
		return !clazz.isPrimitive();
	}
}
