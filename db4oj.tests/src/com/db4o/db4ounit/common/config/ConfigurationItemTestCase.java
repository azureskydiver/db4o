/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.config;

import com.db4o.*;
import com.db4o.config.Configuration;
import com.db4o.config.ConfigurationItem;
import com.db4o.foundation.io.File4;
import com.db4o.foundation.io.Path4;
import com.db4o.internal.*;

import db4ounit.Assert;
import db4ounit.TestCase;

public class ConfigurationItemTestCase implements TestCase {
	
	static final class ConfigurationItemStub implements ConfigurationItem {

		private InternalObjectContainer _container;
		private Configuration _configuration;

		public void apply(InternalObjectContainer container) {
			Assert.isNotNull(container);
			_container = container;
		}

		public void prepare(Configuration configuration) {
			Assert.isNotNull(configuration);
			_configuration = configuration;
		}
		
		public Configuration preparedConfiguration() {
			return _configuration;
		}
		
		public InternalObjectContainer appliedContainer() {
			return _container;
		}
		
	}

	public void test() {
		Configuration configuration = Db4o.newConfiguration();
		
		ConfigurationItemStub item = new ConfigurationItemStub();
		configuration.add(item);
		
		Assert.areSame(configuration, item.preparedConfiguration());
		Assert.isNull(item.appliedContainer());
		
		File4.delete(databaseFile());
		
		ObjectContainer container = Db4o.openFile(configuration, databaseFile());
		container.close();
		
		Assert.areSame(container, item.appliedContainer());
	}

	private String databaseFile() {
		return Path4.combine(Path4.getTempPath(), getClass().getName());
	}
}
