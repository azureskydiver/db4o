package com.db4o.test.replication;

import com.db4o.test.TestSuite;
import com.db4o.test.replication.collections.Db4oListTest;
import com.db4o.test.replication.collections.ListTest;
import com.db4o.test.replication.collections.SimpleArrayTest;
import com.db4o.test.replication.hibernate.ReplicationConfiguratorTest;
import com.db4o.test.replication.hibernate.TablesCreatorTest;
import com.db4o.test.replication.provider.ReplicationProviderTest;
import com.db4o.test.replication.r0tor4.R0to4Runner;

public class ReplicationTestSuite extends TestSuite {
	public Class[] tests() {
		return all();
	}

	private Class[] all() {
		return new Class[]{
				//Simple
				Simplest.class,
				ReplicationEventTest.class,
				ReplicationConfiguratorTest.class,
				ReplicationProviderTest.class,
				ReplicationAfterDeletionTest.class,
				SimpleArrayTest.class,
				SimpleParentChild.class,

				//Collection
				ListTest.class,
				Db4oListTest.class,
				
//				MapTest.class,
//				Db4oMapTest.class,
	//			Db4oIdentityMapTest.class,
				
				SingleTypeCollectionReplicationTest.class,
				ArrayReplicationTest.class,
				MixedTypesCollectionReplicationTest.class,
				
				//Complex
				R0to4Runner.class,
				ReplicationFeaturesMain.class,

				//General
				CollectionHandlerImplTest.class,
				ReplicationTraversalTest.class,
				TablesCreatorTest.class,
				DatabaseUnicityTest.class,
		};
	}

	private Class[] one() {
		return new Class[]{
				ReplicationFeaturesMain.class,
				//Db4oMapTest.class,
				//Db4oIdentityMapTest.class,
		};
	}
}