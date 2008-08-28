/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.jdk;

public class ClassLoaderJdkLoader implements JdkLoader {

	private final ClassLoader _loader;
	
	/**
	 * @exclude
	 */
	public ClassLoaderJdkLoader(ClassLoader loader) {
		_loader = loader;
	}

	public Class loadClass(String className) {
		try {
			return (_loader == null ? Class.forName(className) : Class.forName(className, true, _loader));
		} catch (Exception e) {
			// e.printStackTrace();
		} catch (LinkageError e) {
			// e.printStackTrace();
		}
		return null;
	}

	public Object deepClone(Object context) {
		return new ClassLoaderJdkLoader(_loader);
	}

}
