package com.db4o.instrumentation.core;

import EDU.purdue.cs.bloat.editor.*;

public class BloatUtil {

	public static String normalizeClassName(Type type) {
		return normalizeClassName(type.className());
	}

	public static String normalizeClassName(String className) {
		return className.replace('/', '.');
	}

	public static Class classForEditor(ClassEditor ce, ClassLoader loader) throws ClassNotFoundException {
		String clazzName = normalizeClassName(ce.name());
		Class clazz = loader.loadClass(clazzName);
		return clazz;
	}

	public static boolean isPlatformClassName(String name) {
		return name.startsWith("java.") || name.startsWith("javax.")
				|| name.startsWith("sun.");
	}

	private BloatUtil() {
	}

}
