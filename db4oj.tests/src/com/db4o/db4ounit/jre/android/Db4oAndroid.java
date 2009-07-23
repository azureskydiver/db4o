/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre.android;

import com.db4o.config.*;
import com.db4o.io.*;
import com.db4o.reflect.jdk.*;

import db4ounit.extensions.fixtures.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore
public class Db4oAndroid extends Db4oSolo{
	
	protected Configuration newConfiguration() {
		Configuration config = super.newConfiguration();
		config.storage(new FileStorage());
		config.reflectWith(new JdkReflector(this.getClass().getClassLoader()));
		return config;
	}
	
}
