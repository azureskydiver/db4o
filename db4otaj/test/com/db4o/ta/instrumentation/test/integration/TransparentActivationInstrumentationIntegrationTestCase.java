/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test.integration;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import com.db4o.config.*;
import com.db4o.db4ounit.common.ta.collections.*;
import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.reflect.jdk.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TransparentActivationInstrumentationIntegrationTestCase extends AbstractDb4oTestCase {

	private static final int PRIORITY = 42;
	private ClassLoader _classLoader;
	
	protected void configure(Configuration config) {
		ClassLoader baseLoader = TransparentActivationInstrumentationIntegrationTestCase.class.getClassLoader();
		URL[] urls = {};
		ClassFilter filter = new ByNameClassFilter(new String[] { Project.class.getName() , UnitOfWork.class.getName(), PrioritizedProject.class.getName() });
		BloatClassEdit edit = new InjectTransparentActivationEdit(filter);
		_classLoader = new BloatInstrumentingClassLoader(urls, baseLoader, filter, edit);
		config.add(new PagedListSupport());
		config.add(new TransparentActivationSupport());
		config.reflectWith(new JdkReflector(_classLoader));
	}
	
	protected void store() throws Exception {
		Class unitOfWorkClass = _classLoader.loadClass(UnitOfWork.class.getName());
		Constructor unitOfWorkConstructor = unitOfWorkClass.getConstructor(new Class[]{ String.class, Date.class, Date.class });
		unitOfWorkConstructor.setAccessible(true);
		Object unitOfWork = unitOfWorkConstructor.newInstance(new Object[]{ "ta kick-off", new Date(1000), new Date(2000) });

		Class projectClass = _classLoader.loadClass(PrioritizedProject.class.getName());
		Constructor projectConstructor = projectClass.getConstructor(new Class[]{ String.class, Integer.TYPE });
		projectConstructor.setAccessible(true);		
		Object project = projectConstructor.newInstance(new Object[]{ "db4o", new Integer(PRIORITY) });

		Method logWorkDoneMethod = projectClass.getMethod("logWorkDone", new Class[]{ unitOfWorkClass });
		logWorkDoneMethod.setAccessible(true);
		logWorkDoneMethod.invoke(project, new Object[]{ unitOfWork });
		store(project);
	}
	
	public void test() throws Exception {
		final Object project = retrieveOnlyInstance(Project.class);

		Method getPriorityMethod = project.getClass().getDeclaredMethod("getPriority", new Class[]{});
		getPriorityMethod.setAccessible(true);
		Integer priority = (Integer) getPriorityMethod.invoke(project, new Object[]{});
		Assert.areEqual(PRIORITY, priority.intValue());

		Method totalTimeSpentMethod = project.getClass().getMethod("totalTimeSpent", new Class[]{});
		totalTimeSpentMethod.setAccessible(true);
		Long totalTimeSpent = (Long) totalTimeSpentMethod.invoke(project, new Object[]{});
		Assert.areEqual(1000, totalTimeSpent.intValue());
	}
}
