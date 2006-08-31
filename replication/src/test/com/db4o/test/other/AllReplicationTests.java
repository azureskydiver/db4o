/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.other;

import com.db4o.test.replication.db4ounit.DrsTestCase;
import com.db4o.test.replication.db4ounit.DrsTestSuiteBuilder;
import com.db4o.test.replication.db4ounit.fixtures.Db4oClientServerDrsFixture;
import com.db4o.test.replication.db4ounit.fixtures.Db4oDrsFixture;

import db4ounit.TestRunner;
import db4ounit.TestSuite;
import db4ounit.TestSuiteBuilder;

public class AllReplicationTests extends DrsTestCase implements
		TestSuiteBuilder {

	public TestSuite build() {
		return new DrsTestSuiteBuilder(a(), b(), new Class[] {
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

		}).build();
	}

	public static void main(String[] args) {

//		new TestRunner(new DrsTestSuiteBuilder(new Db4oClientServerDrsFixture(
//				"db4o-cs-a", 0xdb40), new Db4oClientServerDrsFixture(
//				"db4o-cs-b", 4455), AllReplicationTests.class)).run();

		new TestRunner(new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-a"),
				new Db4oDrsFixture("db4o-b"), AllReplicationTests.class)).run();

//		new TestRunner(new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-a"),
//				new Db4oClientServerDrsFixture("db4o-cs-b", 4455),
//				AllReplicationTests.class)).run();
	}
}
