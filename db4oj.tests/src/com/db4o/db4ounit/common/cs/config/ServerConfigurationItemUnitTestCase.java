/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.cs.config;

import java.util.*;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.config.*;

import db4ounit.*;

public class ServerConfigurationItemUnitTestCase implements TestLifeCycle {

	private List<DummyConfigurationItem> _applied;
	private ServerConfigurationImpl _config;
	
	public void testPrepareApply() {
		List<DummyConfigurationItem> items = Arrays.asList(
				new DummyConfigurationItem(_applied),
				new DummyConfigurationItem(_applied)
		);
		for (DummyConfigurationItem item : items) {
			_config.addConfigurationItem(item);
			Assert.areEqual(1, item.prepareCount());
		}
		Assert.areEqual(0, _applied.size());
		_config.applyConfigurationItems(new MockServer());
		Assert.areEqual(items, _applied);
		for (DummyConfigurationItem item : items) {
			Assert.areEqual(1, item.prepareCount());
		}
	}
	
	public void testAddTwice() {
		DummyConfigurationItem item = new DummyConfigurationItem(_applied);
		_config.addConfigurationItem(item);
		_config.addConfigurationItem(item);
		_config.applyConfigurationItems(new MockServer());
		Assert.areEqual(1, item.prepareCount());
		Assert.areEqual(Arrays.asList(item), _applied);
	}

	public void setUp() throws Exception {
		_applied = new ArrayList<DummyConfigurationItem>();
		_config = (ServerConfigurationImpl) Db4oClientServer.newServerConfiguration();
	}

	public void tearDown() throws Exception {
	}

	private static class DummyConfigurationItem implements ServerConfigurationItem {
		private int _prepareCount = 0;
		private List<DummyConfigurationItem> _applied;
		
		public DummyConfigurationItem(List<DummyConfigurationItem> applied) {
			_applied = applied;
		}
		
		public void apply(ObjectServer server) {
			_applied.add(this);
		}

		public void prepare(ServerConfiguration configuration) {
			_prepareCount++;
		}
		
		public int prepareCount() {
			return _prepareCount;
		}
	}
}
