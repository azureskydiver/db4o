/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.ta;

import java.util.*;

import com.db4o.config.*;
import com.db4o.db4ounit.common.ta.collections.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class TransparentActivationTestCase extends AbstractDb4oTestCase implements OptOutCS {

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
