/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package db4ounit.extensions;

import java.lang.reflect.Method;

import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.config.Configure;

import db4ounit.Test;
import db4ounit.TestPlatform;
import db4ounit.extensions.fixtures.*;

public class CSTestSuiteBuilder extends Db4oTestSuiteBuilder {

	public CSTestSuiteBuilder(Class[] classes) {
		// generate fixture here and pass the fixture as a paramtet to super

		super(new Db4oMultiClient(), classes);
	}

	protected Test createTest(Object instance, Method method) {
		return new CSTestMethod(instance, method);
	}

	protected boolean isTestMethod(Method method) {
		String name = method.getName();
		if (startsWithIgnoreCase(name, Configure.COCURRENCY_TEST_PREFIX)) {
			return TestPlatform.isPublic(method)
					&& !TestPlatform.isStatic(method)
					&& hasValidParameter(method);
		}
		return super.isTestMethod(method);
	}

	static boolean hasValidParameter(Method method) {
		Class[] parameters = method.getParameterTypes();
		if (parameters.length == 1 && parameters[0] == ExtObjectContainer.class)
			return true;
		
		if (parameters.length == 2 && parameters[0] == ExtObjectContainer.class
				&& parameters[1] == Integer.TYPE) 
			return true;
		
		return false;
	}
}
