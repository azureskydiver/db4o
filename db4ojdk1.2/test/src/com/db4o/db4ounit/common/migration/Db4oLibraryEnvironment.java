/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.migration;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

import com.db4o.db4ounit.util.*;

/**
 * @sharpen.ignore
 */
public class Db4oLibraryEnvironment {
	
	private final static String[] PREFIXES = { "com.db4o" };
	private final ClassLoader _loader;
	
	public Db4oLibraryEnvironment(File db4oLibrary) throws IOException {
		_loader = getVersionClassLoader(db4oLibrary.toURL());
	}
	
	public String version() throws Exception {
		String version = (String)invokeStaticMethod("com.db4o.Db4o", "version");
        return version.replace(' ', '_');
	}
	
	private static ClassLoader getVersionClassLoader(URL url) throws MalformedURLException {
		URL[] urls = new URL[] { testBinPath().toURL(), url, };
		return new VersionClassLoader(urls, PREFIXES);
	}

	private static File testBinPath() {
		return new File(
				IOServices.safeCanonicalPath(
						System.getProperty("db4oj.tests.bin", "../db4oj.tests/bin")));
	}

	private Object invokeStaticMethod(String className, String methodName) throws Exception {
        Class clazz = _loader.loadClass(className);
        Method method = clazz.getMethod(methodName, new Class[] {});
        return method.invoke(null, new Object[] {});
	}
	
	public Object invokeInstanceMethod(Class klass, String methodName, Object[] args) throws Exception {
		Class clazz = _loader.loadClass(klass.getName());
        Method method = clazz.getMethod(methodName, classes(args));
        return method.invoke(clazz.newInstance(), args);
	}

	private Class[] classes(Object[] args) {
		Class[] classes = new Class[args.length];
		for (int i=0; i<args.length; ++i) {
			classes[i] = args[i].getClass();
		}
		return classes;
	}	
}
