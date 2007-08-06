/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre11.tools;

import com.db4o.ObjectSet;
import com.db4o.events.Event4;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistryFactory;
import com.db4o.query.Query;
import com.db4o.tools.QueryStats;

import db4ounit.Assert;
import db4ounit.extensions.*;

public class QueryStatsTestCase extends AbstractDb4oTestCase {	
	
	public static class Item {
	}
	
	private static final int ITEM_COUNT = 10;
	private QueryStats _stats;
	
	final EventListener4 _sleepOnQueryStart = new EventListener4() {
		public void onEvent(com.db4o.events.Event4 e, com.db4o.events.EventArgs args) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException x) {
				x.printStackTrace();
			}
		}
	};

	protected void store() {
		for (int i=0; i<ITEM_COUNT; ++i) {
			db().set(new Item());
		}
	}
	
	protected void db4oSetupAfterStore() throws Exception {
		_stats = new QueryStats();		
		_stats.connect(stream());
	}

	protected void db4oTearDownBeforeClean() throws Exception {
		_stats.disconnect();
	}

	public void testActivationCount() {
		
		Query q = db().query();		
		q.constrain(Item.class);
		
		ObjectSet result = q.execute();
		Assert.areEqual(0, _stats.activationCount());
		result.next();
		
		if (isClientServer()  && !isMTOC()) {
			Assert.areEqual(10, _stats.activationCount());
		} else {
			Assert.areEqual(1, _stats.activationCount());
			result.next();
			Assert.areEqual(2, _stats.activationCount());
		}
	}

	public void testExecutionTime() {
		
		sleepOnQueryStart();
		
		Query q = db().query();		
		q.constrain(Item.class);		
		
		long started = System.currentTimeMillis();		
		q.execute();		
		long elapsed = System.currentTimeMillis() - started;
		Assert.isTrue(_stats.executionTime() >= 0);
		Assert.isTrue(_stats.executionTime() <= elapsed);
	}

	private void sleepOnQueryStart() {
		queryStartedEvent().addListener(_sleepOnQueryStart);
	}	
	
	private Event4 queryStartedEvent() {
		return EventRegistryFactory.forObjectContainer(fileSession()).queryStarted();
	}

	public static void main(String[] args) {
		new QueryStatsTestCase().runSoloAndClientServer();
	}
}
