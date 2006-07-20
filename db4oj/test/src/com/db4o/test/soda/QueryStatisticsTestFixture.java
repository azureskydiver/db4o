package com.db4o.test.soda;


import com.db4o.query.Query;
import com.db4o.query.QueryStatistics;

import db4ounit.Assert;
import db4ounit.TestRunner;
import db4ounit.db4o.Db4oTestCase;
import db4ounit.db4o.Db4oTestSuiteBuilder;
import db4ounit.db4o.fixtures.Db4oSolo;

public class QueryStatisticsTestFixture extends Db4oTestCase {
	
	public static class Item {

		public int id;

		public Item(int id) {
			this.id = id;
		}
	}
	
	public void store() {
		for (int i=0; i<1000; ++i) {
			db().set(new Item(i));
		}
	}
	
	public void testNoQueryStatistics() {	
		db().configure().diagnostic().queryStatistics(false);		
		Assert.isNull(db().query().statistics());
	}

	public void testExecutionTime() {
		
		db().configure().diagnostic().queryStatistics(true);
		
		Query q = db().query();
		
		QueryStatistics statistics = q.statistics();
		Assert.isNotNull(statistics);		
		Assert.areEqual(0L, statistics.executionTime());
		
		q.constrain(Item.class);		
		
		Assert.areEqual(0L, statistics.executionTime());
		
		long started = System.currentTimeMillis();
		
		q.execute();
		
		long elapsed = System.currentTimeMillis() - started;
		Assert.isTrue(statistics.executionTime() > 0);
		Assert.isTrue(statistics.executionTime() <= elapsed);
	}

	public static void main(String[] args) {
		new TestRunner(
				new Db4oTestSuiteBuilder(
						new Db4oSolo(),
						QueryStatisticsTestFixture.class)).run();
	}
}
