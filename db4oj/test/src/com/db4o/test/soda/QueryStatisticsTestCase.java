package com.db4o.test.soda;


import com.db4o.Db4o;
import com.db4o.query.Candidate;
import com.db4o.query.Evaluation;
import com.db4o.query.Query;
import com.db4o.query.QueryStatistics;

import db4ounit.Assert;
import db4ounit.TestRunner;
import db4ounit.db4o.Db4oTestCase;
import db4ounit.db4o.Db4oTestSuiteBuilder;
import db4ounit.db4o.fixtures.Db4oSolo;

public class QueryStatisticsTestCase extends Db4oTestCase {
	
	private static final int ITEM_COUNT = 10000;

	public static class Item {

		public int id;

		public Item(int id) {
			this.id = id;
		}
	}
	
	protected void configure() {
		Db4o.configure().diagnostic().queryStatistics(true);
	}
	
	protected void store() {
		for (int i=0; i<ITEM_COUNT; ++i) {
			db().set(new Item(i));
		}
	}
	
	public void testNoQueryStatistics() {	
		db().configure().diagnostic().queryStatistics(false);		
		Assert.isNull(db().query().statistics());
	}
	
	public void testNoActivationCount() {
		Query query = db().query();
		query.constrain(Item.class);
		
		Assert.areEqual(0, query.statistics().activationCount());
		query.execute();
		Assert.areEqual(0, query.statistics().activationCount());
	}
	
	public void testEvaluationActivationCount() {
		Query query = db().query();
		query.constrain(Item.class);		
		query.constrain(new Evaluation() {
			public void evaluate(Candidate candidate) {
				candidate.include(true);
			}
		});
		Assert.areEqual(ITEM_COUNT, query.statistics().activationCount());
	}

	public void testExecutionTime() {
		
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
						QueryStatisticsTestCase.class)).run();
	}
}
