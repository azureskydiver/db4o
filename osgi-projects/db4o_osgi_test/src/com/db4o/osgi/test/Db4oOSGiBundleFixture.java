/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.osgi.test;

import java.io.*;

import org.osgi.framework.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.osgi.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.*;

class Db4oOSGiBundleFixture extends AbstractSoloDb4oFixture {

	private final BundleContext _context;
	private final String _fileName;
	private Configuration _config;
	
	
	public Db4oOSGiBundleFixture(BundleContext context, String fileName) {
		super(new ServiceConfigurationSource(context));
		_context = context;
		_fileName = CrossPlatformServices.databasePath(fileName);
	}

	protected ObjectContainer createDatabase(Configuration config) {
		_config = config;
	    return service(_context).openFile(_config,_fileName);
	}

	private static Db4oService service(BundleContext context) {
		ServiceReference sRef = context.getServiceReference(Db4oService.class.getName());
	    Db4oService dbs = (Db4oService)context.getService(sRef);
		return dbs;
	}

	protected void doClean() {
		_config = null;
		new File(_fileName).delete();
	}

	public void defragment() throws Exception {
		defragment(_fileName);
	}

	public String label() {
		return "OSGi/bundle";
	}

	public boolean accept(Class clazz) {
		return super.accept(clazz)&&(!(OptOutNoFileSystemData.class.isAssignableFrom(clazz)));
	}

	private static class ServiceConfigurationSource implements ConfigurationSource {

		private BundleContext _context;
		
		public ServiceConfigurationSource(BundleContext context) {
			_context = context;
		}
		
		public Configuration config() {
			return service(_context).newConfiguration();
		}
		
	}
}
