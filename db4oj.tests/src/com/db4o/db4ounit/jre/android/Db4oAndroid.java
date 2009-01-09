/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre.android;

import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.io.*;
import com.db4o.reflect.jdk.*;

import db4ounit.extensions.fixtures.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore
public class Db4oAndroid extends Db4oSolo{
	
	public Db4oAndroid(ConfigurationSource configSource) {
		super(configSource);	
	}
	
	public Configuration config() {
		Config4Impl config = (Config4Impl)super.config();
		if(alreadyConfigured(config)){
			return config;
		}
		config.storage(new FileStorage());
		config.reflectWith(new JdkReflector(this.getClass().getClassLoader()));
		return config;
	}

	private boolean alreadyConfigured(Config4Impl config) {
		return config.storage() instanceof FileStorage;
	}
	
}
