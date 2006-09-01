/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.test;

import java.lang.reflect.Method;

import db4ounit.ReflectionTestSuiteBuilder;
import db4ounit.Test;
import db4ounit.TestPlatform;

public class CSTestSuiteBuilder extends ReflectionTestSuiteBuilder {

	public CSTestSuiteBuilder(Class[] classes) {
		super(classes);
	}

	protected Test createTest(Object instance, Method method) {
		return new CSTestMethod(instance, method);
	}

	protected boolean isTestMethod(Method method) {
		String name = method.getName();
		if (name.startsWith(CSTestMethod.COCURRENCY_TEST_PREFIX)) {
			return TestPlatform.isPublic(method)
					&& !TestPlatform.isStatic(method)
					&& hasValidParamter(method);
		}
		return super.isTestMethod(method);
	}

	private static boolean hasValidParamter(Method method) {
		Class[] parameters = method.getParameterTypes();
		if (parameters.length == 0) {
			return true;
		}
		return (parameters.length == 1 && parameters[0] == Integer.TYPE);
	}

}
