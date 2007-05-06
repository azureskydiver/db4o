package com.db4o.ta.instrumentation.test.integration;

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

	protected void configure(Configuration config) {
		config.add(new PagedListSupport());
		config.add(new TransparentActivationSupport());
		ClassLoader baseLoader = TransparentActivationInstrumentationIntegrationTestCase.class.getClassLoader();
		URL[] urls = {};
		ClassFilter filter = new ByNameClassFilter(new String[] { Project.class.getName() , UnitOfWork.class.getName() });
		ClassLoader cl = new BloatInstrumentingClassLoader(urls, baseLoader, filter, new InjectTransparentActivationEdit());
		config.reflectWith(new JdkReflector(cl));
	}
	
	protected void store() throws Exception {
		Project project = new Project("db4o");
		project.logWorkDone(new UnitOfWork("ta kick-off", new Date(1000), new Date(2000)));
		store(project);
	}
	
	public void test() {
		final Project project = (Project) retrieveOnlyInstance(Project.class);
		Assert.areEqual(1000, project.totalTimeSpent());
	}
}
