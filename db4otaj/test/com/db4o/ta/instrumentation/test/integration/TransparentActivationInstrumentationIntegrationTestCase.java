package com.db4o.ta.instrumentation.test.integration;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.instrumentation.*;
import com.db4o.reflect.jdk.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TransparentActivationInstrumentationIntegrationTestCase extends AbstractDb4oTestCase {

	private ClassLoader _classLoader;
	
	protected void configure(Configuration config) {
		ClassLoader baseLoader = TransparentActivationInstrumentationIntegrationTestCase.class.getClassLoader();
		URL[] urls = {};
		ClassFilter filter = new ByNameClassFilter(new String[] { Project.class.getName() , UnitOfWork.class.getName() });
		_classLoader = new BloatInstrumentingClassLoader(urls, baseLoader, filter, new InjectTransparentActivationEdit());
		config.add(new PagedListSupport());
		config.add(new TransparentActivationSupport());
		config.reflectWith(new JdkReflector(_classLoader));
	}
	
	protected void store() throws Exception {
		Class unitOfWorkClass = _classLoader.loadClass(UnitOfWork.class.getName());
		Constructor unitOfWorkConstructor = unitOfWorkClass.getConstructor(new Class[]{ String.class, Date.class, Date.class });
		unitOfWorkConstructor.setAccessible(true);
		Object unitOfWork = unitOfWorkConstructor.newInstance(new Object[]{ "ta kick-off", new Date(1000), new Date(2000) });

		Class projectClass = _classLoader.loadClass(Project.class.getName());
		Constructor projectConstructor = projectClass.getConstructor(new Class[]{ String.class });
		projectConstructor.setAccessible(true);		
		Object project = projectConstructor.newInstance(new Object[]{ "db4o" });

		Method logWorkDoneMethod = projectClass.getDeclaredMethod("logWorkDone", new Class[]{ unitOfWorkClass });
		logWorkDoneMethod.setAccessible(true);
		logWorkDoneMethod.invoke(project, new Object[]{ unitOfWork });
		store(project);
	}
	
	public void test() throws Exception {
		final Object project = retrieveOnlyInstance(Project.class);
		Method totalTimeSpentMethod = project.getClass().getDeclaredMethod("totalTimeSpent", new Class[]{});
		totalTimeSpentMethod.setAccessible(true);
		Long totalTimeSpent = (Long) totalTimeSpentMethod.invoke(project, new Object[]{});
		Assert.areEqual(1000, totalTimeSpent.intValue());
	}
}
