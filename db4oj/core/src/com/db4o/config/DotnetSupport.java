/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.config;

import com.db4o.StaticClass;
import com.db4o.StaticField;
import com.db4o.ext.Db4oDatabase;
import com.db4o.internal.*;

/**
 * Adds the basic configuration settings required to access a
 * .net generated database from java.
 * 
 * The configuration only makes sure that database files can be
 * successfuly open and things like UUIDs can be successfuly
 * retrieved.
 * 
 * @sharpen.ignore
 */
public class DotnetSupport implements ConfigurationItem {

	public void prepare(Configuration configuration) {
		configuration.addAlias(
				new TypeAlias(
						"Db4objects.Db4o.Ext.Db4oDatabase, Db4objects.Db4o",
						Db4oDatabase.class.getName()));
		configuration.addAlias(
				new TypeAlias(
						"Db4objects.Db4o.StaticField, Db4objects.Db4o",
						StaticField.class.getName()));
		configuration.addAlias(
				new TypeAlias(
						"Db4objects.Db4o.StaticClass, Db4objects.Db4o",
						StaticClass.class.getName()));
	}
	
	public void apply(InternalObjectContainer container) {
	}
}
