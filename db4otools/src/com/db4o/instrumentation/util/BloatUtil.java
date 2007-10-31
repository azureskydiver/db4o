package com.db4o.instrumentation.util;

import java.io.*;

import EDU.purdue.cs.bloat.editor.*;

/**
 * @exclude
 */
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

	public static String classNameForPath(String classPath) {
		String className = classPath.substring(0, classPath.length()-".class".length());
		className=className.replace(File.separatorChar,'.');
		return className;
	}

	public static String classPathForName(String className) {
		String classPath = className.replace('.', File.separatorChar);
		return classPath + ".class";
	}

	private BloatUtil() {
	}

}
