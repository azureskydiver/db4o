package com.db4o.test.replication;

import com.db4o.test.TestSuite;
import com.db4o.test.replication.collections.SimpleArrayTest;
import com.db4o.test.replication.hibernate.ReplicationConfiguratorTest;
import com.db4o.test.replication.hibernate.TablesCreatorTest;
import com.db4o.test.replication.r0tor4.R0to4Runner;
import com.db4o.test.replication.provider.ReplicationProviderTest;
import com.db4o.test.replication.provider.ObjectVersionTest;

public class ReplicationTestSuite extends TestSuite {
	public Class[] tests() {
		return one();
	}

	private Class[] all() {
		return new Class[]{
				Simplest.class,
				ReplicationEventTest.class,
				ReplicationConfiguratorTest.class,
				ReplicationProviderTest.class,

				//ListTest.class,
				//Db4oListTest.class,
				//MapTest.class,
				ReplicationAfterDeletionTest.class,
				SimpleArrayTest.class,
				SimpleParentChild.class,
				//SingleTypeCollectionReplicationTest.class,

				//General
				CollectionHandlerImplTest.class,
				ReplicationTraversalTest.class,
				TablesCreatorTest.class,
				GetByUUID.class,
				DatabaseUnicityTest.class,

				R0to4Runner.class,
				ArrayReplicationTest.class,
				MixedTypesCollectionReplicationTest.class,
				ReplicationFeaturesMain.class,

				//Db4oObjectUpdateTest
		};
	}

	private Class[] one() {
		return new Class[]{
			ObjectVersionTest.class,
		};
	}
}