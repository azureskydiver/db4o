package com.db4o.db4ounit;

import com.db4o.ObjectContainer;
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

	protected void store() {
		for (int i=0; i<ITEM_COUNT; ++i) {
			db().set(new Item(i));
		}
	}

	/**
	 * Keeps track of query statistics.
	 */
	static class QueryTracker implements EventListener4 {
		
		StopWatch _watch = new StopWatch();
		EventRegistry _registry = null;

		public void onEvent(Event4 e, EventArgs args) {
			if (e == _registry.queryStarted()) {
				_watch.start();
			} else if (e == _registry.queryFinished()) {
				_watch.stop();
			}
		}
		
		public long executionTime() {
			return _watch.elapsed();
		}

		public void connect(ObjectContainer container) {
			_registry = EventRegistryFactory.forObjectContainer(container);
			_registry.queryStarted().addListener(this);
			_registry.queryFinished().addListener(this);
		}
		
		public void disconnect() {
			if (null != _registry) {
				_registry.queryStarted().removeListener(this);
				_registry.queryFinished().removeListener(this);
				_registry = null;
			}
		}
	}

	public void testExecutionTime() {
		
		QueryTracker listener = new QueryTracker();		
		listener.connect(db());
		
		Query q = db().query();		
		q.constrain(Item.class);		
		
		long started = System.currentTimeMillis();		
		q.execute();		
		long elapsed = System.currentTimeMillis() - started;
		Assert.isTrue(listener.executionTime() > 0);
		Assert.isTrue(listener.executionTime() <= elapsed);
	}

	public static void main(String[] args) {
		new TestRunner(
				new Db4oTestSuiteBuilder(
						new Db4oSolo(),
						QueryStatisticsTestCase.class)).run();
	}
}
