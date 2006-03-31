package com.db4o.test.replication;

import com.db4o.test.TestSuite;
import com.db4o.test.replication.collections.ListTest;
import com.db4o.test.replication.collections.SimpleArrayTest;
import com.db4o.test.replication.collections.map.MapTest;
import com.db4o.test.replication.hibernate.HibernateProviderTest;
import com.db4o.test.replication.hibernate.ReplicationConfiguratorTest;
import com.db4o.test.replication.hibernate.TablesCreatorTest;
import com.db4o.test.replication.provider.TransientReplicationProviderTest;
import com.db4o.test.replication.template.SimpleParentChild;
import com.db4o.test.replication.template.r0tor4.R0to4Runner;

public class ReplicationTestSuite extends TestSuite {

	public Class[] tests2() {
		System.err.println("Use tests2 below");
		return new Class[] {
			ReplicationFeaturesMain.class,
		};
	}

	public Class[] tests() {
		System.err.println("Uncomment Db4oReplicationProviderTest in ReplicationTestSuite");
		return new Class[]{
                
                ArrayReplicationTest.class,
                ListTest.class,
                MapTest.class,
                MixedTypesCollectionReplicationTest.class,
                R0to4Runner.class,
                ReplicationAfterDeletionTest.class,
                ReplicationFeaturesMain.class,
                SimpleArrayTest.class,
                SimpleParentChild.class,
                //SimplePerformanceTests.class,
                SingleTypeCollectionReplicationTest.class,
                
                //General
				CollectionHandlerImplTest.class,
				ReplicationTraversalTest.class,

				//Transient
				TransientReplicationProviderTest.class,

				//Hibernate
				//HibernateReplicationFeaturesMain.class, use MultiProviderReplicationFeaturesMain.class, instead

				HibernateProviderTest.class,
				ReplicationConfiguratorTest.class,
				TablesCreatorTest.class,

				//Db4o
				//Db4oReplicationFeaturesMain.class,
				//TODO Db4oReplicationProviderTest.class,
				GetByUUID.class,
				//Db4oReplicationAfterDeletionTest.class,
				//Db4oListTest.class,
				//Db4oMapTest.class,
				//Db4oSingleTypeCollectionReplicationTest.class,
				//Db4oSimpleArrayTest.class,
				//Db4oArrayReplicationTest.class,
                DatabaseUnicityTest.class,

		};
	}
}