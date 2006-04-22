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
		return all();
	}

	private Class[] all() {
		return new Class[]{
				ReplicationEventTest.class,
				ReplicationConfiguratorTest.class,
				ReplicationProviderTest.class,

				ListTest.class,
				MapTest.class,
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

				//db4o won't pass these
//				R0to4Runner.class,
//				ArrayReplicationTest.class,
//				MixedTypesCollectionReplicationTest.class,
		};
	}

	private Class[] testsOne() {
		return new Class[]{
				ReplicationFeaturesMain.class,
		};
	}
}