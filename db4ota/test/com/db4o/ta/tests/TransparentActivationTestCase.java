/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.tests;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ta.*;
import com.db4o.ta.tests.collections.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TransparentActivationTestCase extends AbstractDb4oTestCase {

	private static final int PRIORITY = 42;

	protected void configure(Configuration config) {
		config.add(new PagedListSupport());
		config.add(new TransparentActivationSupport());
	}
	
	protected void store() throws Exception {
		Project project = new PrioritizedProject("db4o",PRIORITY);
		project.logWorkDone(new UnitOfWork("ta kick-off", new Date(1000), new Date(2000)));
		store(project);
	}
	
	public void test() {
		final PrioritizedProject project = (PrioritizedProject) retrieveOnlyInstance(Project.class);
		
		Assert.areEqual(PRIORITY, project.getPriority());
		// Project.totalTimeSpent needs the UnitOfWork objects to be activated
		Assert.areEqual(1000, project.totalTimeSpent());
	}
}
