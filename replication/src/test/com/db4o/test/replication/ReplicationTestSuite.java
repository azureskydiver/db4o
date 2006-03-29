package com.db4o.test.replication;

import com.db4o.test.*;
import com.db4o.test.replication.db4o.hibernate.*;
import com.db4o.test.replication.hibernate.*;
import com.db4o.test.replication.jdk11.*;
import com.db4o.test.replication.jdk14.*;
import com.db4o.test.replication.provider.*;
import com.db4o.test.replication.transients.*;

public class ReplicationTestSuite extends TestSuite {

	public Class[] tests() {
		System.err.println("Use tests2 below");
		return new Class[]{
                TransientListTest.class,
//                TransientReplicationFeaturesMain.class,
//                HibernateReplicationFeaturesMain.class,
//                MultiProviderReplicationFeaturesMain.class,
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
				//HibernateReplicationFeaturesMain.class, use MultiProviderReplicationFeaturesMain.class, instead

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