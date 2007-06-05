package com.db4o.osgi.test;

import java.util.*;

import org.osgi.framework.*;

public class Db4oTestActivator implements BundleActivator {

	public static final String BUNDLE_ID = "db4o_osgi_test";

	public void start(BundleContext context) {
		context.registerService(
				Db4oTestService.class.getName(), 
				new Db4oTestServiceImpl(context), 
				new Hashtable());		
	}

	public void stop(BundleContext ctx) {
	}

}