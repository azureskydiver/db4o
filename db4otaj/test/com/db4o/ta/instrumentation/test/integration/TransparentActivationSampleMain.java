/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test.integration;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.reflect.jdk.*;
import com.db4o.ta.*;
import com.db4o.ta.tests.collections.*;

import db4ounit.extensions.*;

public class TransparentActivationSampleMain extends AbstractDb4oTestCase {

	private static final int PRIORITY = 42;
	private static final String FILENAME = "ta.db4o";

	public static void main(String[] args) {
		Configuration config = Db4o.newConfiguration();
		config.add(new PagedListSupport());
		config.add(new TransparentActivationSupport());
		config.reflectWith(new JdkReflector(TransparentActivationSampleMain.class.getClassLoader()));
		
		new File(FILENAME).delete();
		ObjectContainer db = Db4o.openFile(config, FILENAME);
		PrioritizedProject project = new PrioritizedProject("db4o",PRIORITY);
		project.logWorkDone(new UnitOfWork("ta kick-off", new Date(1000), new Date(2000)));
		db.set(project);
		db.close();
		project = null;

		db = Db4o.openFile(config, FILENAME);
		ObjectSet result = db.query(PrioritizedProject.class);
		System.out.println(result.size());
		project = (PrioritizedProject) result.next();
		System.out.println(project.getPriority());
		System.out.println(project.totalTimeSpent());
		db.close();
		new File(FILENAME).delete();
	}
}
