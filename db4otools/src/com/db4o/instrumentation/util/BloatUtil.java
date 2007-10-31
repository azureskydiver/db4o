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
		return loader.loadClass(clazzName);
	}

	public static boolean isPlatformClassName(String name) {
		return name.startsWith("java.") || name.startsWith("javax.")
				|| name.startsWith("sun.");
	}

	public static String classNameForPath(String classPath) {
		String className = classPath.substring(0, classPath.length()-".class".length());
		return className.replace(File.separatorChar,'.');
	}

	private BloatUtil() {
	}

}
