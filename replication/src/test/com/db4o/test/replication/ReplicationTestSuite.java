package com.db4o.test.replication;

import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.Db4oArrayReplicationTest;
import com.db4o.test.replication.db4o.Db4oListTest;
import com.db4o.test.replication.db4o.Db4oMapTest;
import com.db4o.test.replication.db4o.Db4oReplicationAfterDeletionTest;
import com.db4o.test.replication.db4o.Db4oReplicationFeaturesMain;
import com.db4o.test.replication.db4o.Db4oSimpleArrayTest;
import com.db4o.test.replication.db4o.Db4oSingleTypeCollectionReplicationTest;
import com.db4o.test.replication.db4o.hibernate.Db4oHibernateListTest;
import com.db4o.test.replication.db4o.hibernate.Db4oHibernateSimpleArrayTest;
import com.db4o.test.replication.db4o.hibernate.HibernateDb4oListTest;
import com.db4o.test.replication.hibernate.ref_as_table.ReplicationConfiguratorTestRefAsTable;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.AfterDeletionTestHsqlRefAsTable;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.FeaturesMainRefAsTable;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.ListTestHsqlRefAsTable;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.MapTestHsqlRefAsTable;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.MapTestRefAsTableDb4o;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.ProviderTestHsqlRefAsTable;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.RefAsTableTablesCreatorTestHsql;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.SimpleArrayTestHsqlRefAsTable;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.SingleTypeCollectionTestHsqlRefAsTable;
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

	public Class[] tests2() {
		System.err.println("Use tests2 below");
		return new Class[]{
				TransientReplicationFeaturesMain.class,
		};
	}

	public Class[] tests() {
		System.err.println("Uncomment Db4oReplicationProviderTest in ReplicationTestSuite");
		return new Class[]{
				TransientReplicationFeaturesMain.class,
				Db4oReplicationFeaturesMain.class,
				FeaturesMainRefAsTable.class,

				TransientReplicationProviderTest.class,
				//TODO Db4oReplicationProviderTest.class,
				ProviderTestHsqlRefAsTable.class,

				ReplicationConfiguratorTestRefAsTable.class,

				CollectionHandlerImplTest.class,
				GetByUUID.class,

				RefAsTableTablesCreatorTestHsql.class,

				Db4oReplicationAfterDeletionTest.class,
				AfterDeletionTestHsqlRefAsTable.class,

				R0to4RunnerCore.class,
				R0to4RunnerCombinations.class,

				SimpleParentChildCore.class,
				SimpleParentChildCombinations.class,

				TransientListTest.class,
				Db4oListTest.class,
				HibernateDb4oListTest.class,
				Db4oHibernateListTest.class,
				ListTestHsqlRefAsTable.class,

				TransientMapTest.class,
				Db4oMapTest.class,
				MapTestHsqlRefAsTable.class,
				MapTestRefAsTableDb4o.class,

				TransientSimpleArrayTest.class,
				Db4oSimpleArrayTest.class,
				Db4oHibernateSimpleArrayTest.class,
				SimpleArrayTestHsqlRefAsTable.class,

				TransientSingleTypeCollectionReplicationTest.class,
				Db4oSingleTypeCollectionReplicationTest.class,
				SingleTypeCollectionTestHsqlRefAsTable.class,

				TransientMixedTypesCollectionReplicationTest.class,

				TransientArrayReplicationTest.class,
				Db4oArrayReplicationTest.class,

				ReplicationTraversalTest.class,
				ReplicationFeatures.class,
		};
	}
}