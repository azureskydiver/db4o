/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.config.Configuration;

import db4ounit.extensions.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore
public class Db4oAndroid extends Db4oSolo{
	
	protected Configuration newConfiguration() {
		Configuration config = super.newConfiguration();
		return config;
	}
	
	@Override
	public boolean accept(Class clazz) {
		return !OptOutTemporary.class.isAssignableFrom(clazz) && super.accept(clazz);
	}
	
}
