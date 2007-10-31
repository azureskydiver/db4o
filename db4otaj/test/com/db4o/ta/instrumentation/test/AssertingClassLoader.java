/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.ta.instrumentation.test;

import java.io.*;
import java.net.*;

import com.db4o.test.util.*;

import db4ounit.*;

/**
 * Creates a separate environment to load classes ({@link ExcludingClassLoader}
 * so they can be asserted after instrumentation.
 */
public class AssertingClassLoader {

	private final URLClassLoader _loader;

	public AssertingClassLoader(File classPath, Class[] excludedClasses) throws MalformedURLException {
		ExcludingClassLoader excludingLoader = new ExcludingClassLoader(getClass().getClassLoader(), excludedClasses);		
		_loader = new URLClassLoader(new URL[] { toURL(classPath) }, excludingLoader);
	}

	/**
	 * @deprecated
	 */
	private URL toURL(File classPath) throws MalformedURLException {
		return classPath.toURL();
	}

	public void assertAssignableFrom(Class expected, Class actual) throws ClassNotFoundException {
		if (isAssignableFrom(expected, actual)) {
			return;
		}
		
		fail(expected, actual, "not assignable from");
	}

	public void assertNotAssignableFrom(Class expected, Class actual) throws ClassNotFoundException {
		if (!isAssignableFrom(expected, actual)) {
			return;
		}
		
		fail(expected, actual, "assignable from");
	}
	
	private void fail(Class expected, Class actual, String reason) {
		Assert.fail("'" + actual + "' " + reason + " '" + expected + "'");
	}

	private boolean isAssignableFrom(Class expected, Class actual) throws ClassNotFoundException {
		return expected.isAssignableFrom(loadClass(actual));
	}

	private Class loadClass(Class actual) throws ClassNotFoundException {
		return _loader.loadClass(actual.getName());
	}
}
