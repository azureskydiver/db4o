package com.db4o.osgi.test;

import java.util.*;

import org.osgi.framework.*;

import db4ounit.TestRunner;
import db4ounit.TestSuite;
import db4ounit.extensions.Db4oTestSuiteBuilder;

public class Db4oTestActivator implements BundleActivator {

	public static final String BUNDLE_ID = "db4o_osgi_test";
	  private static final String FILENAME = "db4ounit.osgi.db4o";

	public void start(BundleContext context) {
		context.registerService(
				Db4oTestService.class.getName(), 
				new Db4oTestServiceImpl(context), 
				new Hashtable());
				
		
		Db4oOSGiBundleFixture fixture = new Db4oOSGiBundleFixture(context, FILENAME);
		Class testClass = 
				com.db4o.db4ounit.jre12.AllTestsJdk1_2.class;
		TestSuite suite = new Db4oTestSuiteBuilder(fixture, testClass).build();
		System.exit((new TestRunner(suite).run()) > 0 ? 1 : 0);
		
	}

	public void stop(BundleContext ctx) {
	}

}