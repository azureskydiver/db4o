/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.replication.db4ounit;

import com.db4o.test.drs.ArrayReplicationTest;
import com.db4o.test.drs.CollectionHandlerImplTest;
import com.db4o.test.drs.Db4oListTest;
import com.db4o.test.drs.ListTest;
import com.db4o.test.drs.MapTest;
import com.db4o.test.drs.MixedTypesCollectionReplicationTest;
import com.db4o.test.drs.R0to4Runner;
import com.db4o.test.drs.ReplicationAfterDeletionTest;
import com.db4o.test.drs.ReplicationEventTest;
import com.db4o.test.drs.ReplicationFeaturesMain;
import com.db4o.test.drs.ReplicationProviderTest;
import com.db4o.test.drs.ReplicationTraversalTest;
import com.db4o.test.drs.SimpleArrayTest;
import com.db4o.test.drs.SimpleParentChild;
import com.db4o.test.drs.SingleTypeCollectionReplicationTest;
import com.db4o.test.drs.TheSimplest;

import db4ounit.TestSuite;
import db4ounit.TestSuiteBuilder;

/**
 * @exclude
 */
public abstract class DrsTestSuite extends DrsTestCase implements
		TestSuiteBuilder {

	public TestSuite build() {
		return new DrsTestSuiteBuilder(a(), b(), testCases()).build();
	}

	protected Class[] testCases() {
		return all();
	}

	protected Class[] one() {
		return new Class[] { ArrayReplicationTest.class, };
	}

	protected Class[] all() {
		return new Class[] {
				// Simple
				TheSimplest.class, ReplicationEventTest.class,
				ReplicationProviderTest.class,
				ReplicationAfterDeletionTest.class,
				SimpleArrayTest.class,
				SimpleParentChild.class,

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
				CollectionHandlerImplTest.class, ReplicationTraversalTest.class };
	}
}
