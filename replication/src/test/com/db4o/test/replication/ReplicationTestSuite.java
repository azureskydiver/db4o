package com.db4o.test.replication;

import com.db4o.test.TestSuite;
import com.db4o.test.replication.collections.ListTest;
import com.db4o.test.replication.collections.SimpleArrayTest;
import com.db4o.test.replication.collections.map.MapTest;
import com.db4o.test.replication.hibernate.ReplicationConfiguratorTest;
import com.db4o.test.replication.hibernate.TablesCreatorTest;
import com.db4o.test.replication.r0tor4.R0to4Runner;
import com.db4o.test.replication.provider.ReplicationProviderTest;

public class ReplicationTestSuite extends TestSuite {
	public Class[] tests() {
		return db4o();
	}

	public Class[] testsOne() {
		return new Class[]{
				ReplicationFeaturesMain.class,
		};
	}

	private static Class[] all() {
		return new Class[]{
				ReplicationEventTest.class,
				ReplicationConfiguratorTest.class,
				ReplicationProviderTest.class,

				ArrayReplicationTest.class,
				ListTest.class,
				MapTest.class,
				MixedTypesCollectionReplicationTest.class,
				R0to4Runner.class,
				ReplicationAfterDeletionTest.class,
				ReplicationFeaturesMain.class,
				SimpleArrayTest.class,
				SimpleParentChild.class,
				SingleTypeCollectionReplicationTest.class,

				//General
				CollectionHandlerImplTest.class,
				ReplicationTraversalTest.class,
				TablesCreatorTest.class,
				GetByUUID.class,
				DatabaseUnicityTest.class,
		};
	}

	public Class[] db4o() {
		return new Class[]{
				ReplicationEventTest.class,
				ReplicationConfiguratorTest.class,
				ReplicationProviderTest.class,

				//TODO StackOverflowError when running with db4o ArrayReplicationTest.class,
				ListTest.class,
				MapTest.class,
				//TODO StackOverflowError when running with db4o MixedTypesCollectionReplicationTest.class,
				//TODO StackOverflowError when running with db4o R0to4Runner.class,
				ReplicationAfterDeletionTest.class,
				ReplicationFeaturesMain.class,
				SimpleArrayTest.class,
				SimpleParentChild.class,
				SingleTypeCollectionReplicationTest.class,

				//General
				CollectionHandlerImplTest.class,
				ReplicationTraversalTest.class,
				TablesCreatorTest.class,
				GetByUUID.class,
				DatabaseUnicityTest.class,
		};
	}
}