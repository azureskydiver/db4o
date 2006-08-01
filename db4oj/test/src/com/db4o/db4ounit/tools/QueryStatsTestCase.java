/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.tools;

import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.db4o.tools.QueryStats;

import db4ounit.Assert;
import db4ounit.TestRunner;
import db4ounit.db4o.Db4oTestCase;
import db4ounit.db4o.Db4oTestSuiteBuilder;
import db4ounit.db4o.fixtures.Db4oSolo;

public class QueryStatsTestCase extends Db4oTestCase {
	
	public static class Item {
	}
	
	private static final int ITEM_COUNT = 10000;
	private QueryStats _stats;

	protected void store() {
		for (int i=0; i<ITEM_COUNT; ++i) {
			db().set(new Item());
		}
	}
	
	public void setUp() throws Exception {
		super.setUp();
		
		_stats = new QueryStats();		
		_stats.connect(db());
	}
	
	public void tearDown() throws Exception {
		
		_stats.disconnect();
		
		super.tearDown();
	}

	public void testActivationCount() {
		
		Query q = db().query();		
		q.constrain(Item.class);
		
		ObjectSet result = q.execute();
		
		Assert.areEqual(0, _stats.activationCount());		
		result.next();		
		Assert.areEqual(1, _stats.activationCount());
		result.next();
		Assert.areEqual(2, _stats.activationCount());
	}

	public void testExecutionTime() {
		
		Query q = db().query();		
		q.constrain(Item.class);		
		
		long started = System.currentTimeMillis();		
		q.execute();		
		long elapsed = System.currentTimeMillis() - started;
		Assert.isTrue(_stats.executionTime() > 0);
		Assert.isTrue(_stats.executionTime() <= elapsed);
	}

	public static void main(String[] args) {
		new TestRunner(
				new Db4oTestSuiteBuilder(
						new Db4oSolo(),
						QueryStatsTestCase.class)).run();
	}
}
