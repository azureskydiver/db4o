/* Copyright (C) 2009 Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.config;

import com.db4o.Db4o;
import com.db4o.internal.Config4Impl;

import db4ounit.Assert;
import db4ounit.ConsoleTestRunner;
import db4ounit.TestCase;

public class Config4ImplTestCase implements TestCase {
	public static void main(String[] args) {
		new ConsoleTestRunner(Config4ImplTestCase.class).run();
	}
	
	/**
	 * @deprecated Test uses deprecated API 
	 */
	public void testReadAsKeyIsolation() {
		Config4Impl config1 = (Config4Impl) Db4o.newConfiguration();
		Config4Impl config2 = (Config4Impl) Db4o.newConfiguration();
		
		Assert.areNotSame(config1.readAs(), config2.readAs());		
	}
}
