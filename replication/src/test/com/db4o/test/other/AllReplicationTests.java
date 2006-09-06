/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.other;

import com.db4o.test.replication.db4ounit.DrsTestSuite;

public class AllReplicationTests extends DrsTestSuite {

	protected Class[] testCases() {
		return new Class[] {
				// Bugs
				BugDrs8.class,

				// // Simple
				TheSimplest.class,
				ReplicationEventTest.class,
				ReplicationProviderTest.class,
				ReplicationAfterDeletionTest.class,
				SimpleArrayTest.class,
				SimpleParentChild.class,
				GetByUUID.class,

				// Collection
				MapTest.class,
				ArrayReplicationTest.class,
				// CollectionUuidTest.class,
				ListTest.class, Db4oListTest.class, MapTest.class,
				SingleTypeCollectionReplicationTest.class,
				MixedTypesCollectionReplicationTest.class,

				// Complex
				R0to4Runner.class, ReplicationFeaturesMain.class,

				// General
				CollectionHandlerImplTest.class,
				ReplicationTraversalTest.class, DatabaseUnicityTest.class

		};
	}

	public static void main(String[] args) {

//		new TestRunner(new DrsTestSuiteBuilder(new Db4oClientServerDrsFixture(
//				"db4o-cs-a", 0xdb40), new Db4oClientServerDrsFixture(
//				"db4o-cs-b", 4455), AllReplicationTests.class)).run();

		new AllReplicationTests().runDb4oDb4o();

//		new TestRunner(new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-a"),
//				new Db4oClientServerDrsFixture("db4o-cs-b", 4455),
//				AllReplicationTests.class)).run();
	}
}
