package com.db4o.test.replication;

import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.hibernate.Db4oHibernateListTest;
import com.db4o.test.replication.hibernate.HibernateAfterDeletion;
import com.db4o.test.replication.hibernate.HibernateListTest;
import com.db4o.test.replication.hibernate.HibernateMapTest;
import com.db4o.test.replication.hibernate.HibernateProviderTest;
import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;
import com.db4o.test.replication.hibernate.HibernateSimpleArrayTest;
import com.db4o.test.replication.hibernate.HibernateSingleTypeCollectionTest;
import com.db4o.test.replication.hibernate.ReplicationConfiguratorTest;
import com.db4o.test.replication.hibernate.TablesCreatorTest;
import com.db4o.test.replication.jdk11.R0to4RunnerCore;
import com.db4o.test.replication.jdk11.SimpleParentChildCore;
import com.db4o.test.replication.jdk14.R0to4RunnerCombinations;
import com.db4o.test.replication.jdk14.SimpleParentChildCombinations;
import com.db4o.test.replication.provider.TransientReplicationProviderTest;
import com.db4o.test.replication.transients.TransientArrayReplicationTest;
import com.db4o.test.replication.transients.TransientListTest;
import com.db4o.test.replication.transients.TransientMapTest;
import com.db4o.test.replication.transients.TransientMixedTypesCollectionReplicationTest;
import com.db4o.test.replication.transients.TransientReplicationFeaturesMain;
import com.db4o.test.replication.transients.TransientSimpleArrayTest;
import com.db4o.test.replication.transients.TransientSingleTypeCollectionReplicationTest;

public class ReplicationTestSuite extends TestSuite {

	public Class[] tests() {
		System.err.println("Use tests2 below");
		return new Class[]{
				TransientReplicationFeaturesMain.class,
				HibernateReplicationFeaturesMain.class,
		};
	}

	public Class[] tests2() {
		System.err.println("Uncomment Db4oReplicationProviderTest in ReplicationTestSuite");
		return new Class[]{
				//General
				CollectionHandlerImplTest.class,
				ReplicationTraversalTest.class,
				ReplicationFeatures.class,

				//Transient
				//TransientReplicationFeaturesMain.class,
				TransientReplicationProviderTest.class,
				TransientListTest.class,
				TransientMapTest.class,
				TransientMixedTypesCollectionReplicationTest.class,
				TransientArrayReplicationTest.class,
				TransientSingleTypeCollectionReplicationTest.class,
				TransientSimpleArrayTest.class,

				//Hibernate
				//HibernateReplicationFeaturesMain.class,
				HibernateProviderTest.class,
				ReplicationConfiguratorTest.class,
				TablesCreatorTest.class,
				HibernateAfterDeletion.class,
				HibernateListTest.class,
				HibernateSingleTypeCollectionTest.class,
				HibernateSimpleArrayTest.class,
				HibernateMapTest.class,

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

				//Mixed
				R0to4RunnerCore.class,
				R0to4RunnerCombinations.class,

				SimpleParentChildCore.class,
				SimpleParentChildCombinations.class,

				Db4oHibernateListTest.class,

				//Db4oHibernateMapTest.class,
				//Db4oHibernateSimpleArrayTest.class,
				//HibernateDb4oListTest.class,
		};
	}
}