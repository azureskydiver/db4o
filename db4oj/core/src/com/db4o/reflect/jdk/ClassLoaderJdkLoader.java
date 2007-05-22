/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.jdk;

public class ClassLoaderJdkLoader implements JdkLoader {

	private final ClassLoader _loader;
	
	public ClassLoaderJdkLoader(ClassLoader loader) {
		_loader = loader;
	}

	public Class loadClass(String className) throws ClassNotFoundException {
		return _loader.loadClass(className);
	}

	public Object deepClone(Object context) {
		return new ClassLoaderJdkLoader(_loader);
	}

}
