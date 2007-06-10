/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4o.osgi;

import java.util.*;

import org.osgi.framework.*;

public class Db4oActivator implements BundleActivator {

	public final static String BUNDLE_ID = "db4o_osgi";
	
	public void start(BundleContext context) throws Exception {
		context.registerService(
				Db4oService.class.getName(), 
				new Db4oServiceFactory(), 
				new Hashtable());		
	}
	
	public void stop(BundleContext context) throws Exception {
	}

}
