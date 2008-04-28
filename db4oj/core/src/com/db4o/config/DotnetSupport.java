/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.config;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;

/**
 * Adds the basic configuration settings required to access a
 * .net generated database from java.
 * 
 * The configuration only makes sure that database files can be
 * successfully open and things like UUIDs can be successfully
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
		NetTypeHandler[] handlers = Platform4.jdk().netTypes(container.reflector());
		for (int netTypeIdx = 0; netTypeIdx < handlers.length; netTypeIdx++) {
			NetTypeHandler handler = handlers[netTypeIdx];
			container.handlers().registerNetTypeHandler(handler);
		}
	}
}
