/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.events.*;
import com.db4o.foundation.StopWatch;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.TestRunner;
import db4ounit.db4o.Db4oTestCase;
import db4ounit.db4o.Db4oTestSuiteBuilder;
import db4ounit.db4o.fixtures.Db4oSolo;

public class QueryStatisticsTestCase extends Db4oTestCase {
	
	private static final int ITEM_COUNT = 10000;
	private QueryTracker _tracker;

	protected void store() {
		for (int i=0; i<ITEM_COUNT; ++i) {
			db().set(new Item(i));
		}
	}
	
	public void setUp() throws Exception {
		super.setUp();
		
		_tracker = new QueryTracker();		
		_tracker.connect(db());
	}
	
	public void tearDown() throws Exception {
		
		_tracker.disconnect();
		
		super.tearDown();
	}

	/**
	 * Keeps track of query statistics.
	 */
	static class QueryTracker {	
		
		private EventRegistry _registry = null;
		
		private int _activationCount;
		
		private final StopWatch _watch = new StopWatch();
		
		private final EventListener4 _queryStarted = new EventListener4() {			
			public void onEvent(Event4 e, EventArgs args) {
				_activationCount = 0;
				_watch.start();
			}			
		};
		
		private final EventListener4 _queryFinished = new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				_watch.stop();
			}
		};
		
		private final EventListener4 _activated = new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				++_activationCount;
			};
		};

		public long executionTime() {
			return _watch.elapsed();
		}
		
		public int activationCount() {
			return _activationCount;
		}

		public void connect(ObjectContainer container) {
			_registry = EventRegistryFactory.forObjectContainer(container);
			_registry.queryStarted().addListener(_queryStarted);
			_registry.queryFinished().addListener(_queryFinished);
			_registry.activated().addListener(_activated);
		}
		
		public void disconnect() {
			if (null != _registry) {
				_registry.queryStarted().removeListener(_queryStarted);
				_registry.queryFinished().removeListener(_queryFinished);
				_registry.activated().removeListener(_activated);
				_registry = null;
			}
		}
	}
	
	public void testActivationCount() {
		
		Query q = db().query();		
		q.constrain(Item.class);
		
		ObjectSet result = q.execute();
		
		Assert.areEqual(0, _tracker.activationCount());		
		result.next();		
		Assert.areEqual(1, _tracker.activationCount());
		result.next();
		Assert.areEqual(2, _tracker.activationCount());
		
	}

	public void testExecutionTime() {
		
		Query q = db().query();		
		q.constrain(Item.class);		
		
		long started = System.currentTimeMillis();		
		q.execute();		
		long elapsed = System.currentTimeMillis() - started;
		Assert.isTrue(_tracker.executionTime() > 0);
		Assert.isTrue(_tracker.executionTime() <= elapsed);
	}

	public static void main(String[] args) {
		new TestRunner(
				new Db4oTestSuiteBuilder(
						new Db4oSolo(),
						QueryStatisticsTestCase.class)).run();
	}
}
