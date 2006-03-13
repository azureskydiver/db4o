package com.db4o.test.replication;

import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.Db4oArrayReplicationTest;
import com.db4o.test.replication.db4o.Db4oListTest;
import com.db4o.test.replication.db4o.Db4oMapTest;
import com.db4o.test.replication.db4o.Db4oReplicationAfterDeletionTest;
import com.db4o.test.replication.db4o.Db4oReplicationFeaturesMain;
import com.db4o.test.replication.db4o.Db4oReplicationProviderTest;
import com.db4o.test.replication.db4o.Db4oSimpleArrayTest;
import com.db4o.test.replication.db4o.Db4oSingleTypeCollectionReplicationTest;
import com.db4o.test.replication.db4o.hibernate.Db4oHibernateListTest;
import com.db4o.test.replication.db4o.hibernate.Db4oHibernateSimpleArrayTest;
import com.db4o.test.replication.db4o.hibernate.HibernateDb4oListTest;
import com.db4o.test.replication.hibernate.UuidLongPartGeneratorTest;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.Db4oHibernateMapTest;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HibernateDb4oMapTest;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HibernateReplicationAfterDeletionTest;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HibernateReplicationFeaturesMain;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HibernateReplicationProviderTest;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HibernateSimpleArrayTest;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HibernateSingleTypeCollectionReplicationTest;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HsqlMetaDataTablesCreatorTest;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.ListTestHsqlRefAsColumns;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.MapTestHsqlRefAsColumns;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.ReplicationConfiguratorTest;
import com.db4o.test.replication.hibernate.ref_as_table.RefAsTableReplicationConfiguratorTest;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.AfterDeletionTestHsqlRefAsTable;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.ListTestHsqlRefAsTable;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.MapTestHsqlRefAsTable;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.RefAsTableReplicationFeaturesMain;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.RefAsTableTablesCreatorTestHsql;
import com.db4o.test.replication.hibernate.ref_as_table.hsql.ReplicationProviderTestHsqlRefAsTable;
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

	public Class[] tests() {
		return new Class[]{
				TransientReplicationFeaturesMain.class,
				Db4oReplicationFeaturesMain.class,
				HibernateReplicationFeaturesMain.class,
				RefAsTableReplicationFeaturesMain.class,

				CollectionHandlerImplTest.class,
				GetByUUID.class,

				HsqlMetaDataTablesCreatorTest.class,
				RefAsTableTablesCreatorTestHsql.class,

				HibernateReplicationAfterDeletionTest.class,
				Db4oReplicationAfterDeletionTest.class,
				AfterDeletionTestHsqlRefAsTable.class,

				TransientReplicationProviderTest.class,
				Db4oReplicationProviderTest.class,
				HibernateReplicationProviderTest.class,
				ReplicationProviderTestHsqlRefAsTable.class,

				R0to4RunnerCore.class,
				R0to4RunnerCombinations.class,

				SimpleParentChildCore.class,
				SimpleParentChildCombinations.class,

				TransientListTest.class,
				Db4oListTest.class,
				ListTestHsqlRefAsColumns.class,
				HibernateDb4oListTest.class,
				Db4oHibernateListTest.class,
				ListTestHsqlRefAsTable.class,

				TransientMapTest.class,
				Db4oMapTest.class,
				MapTestHsqlRefAsColumns.class,
				HibernateDb4oMapTest.class,
				Db4oHibernateMapTest.class,
				MapTestHsqlRefAsTable.class,

				TransientSimpleArrayTest.class,
				Db4oSimpleArrayTest.class,
				HibernateSimpleArrayTest.class,
				Db4oHibernateSimpleArrayTest.class,
				SimpleArrayTestHsqlRefAsTable.class,

				TransientSingleTypeCollectionReplicationTest.class,
				Db4oSingleTypeCollectionReplicationTest.class,
				HibernateSingleTypeCollectionReplicationTest.class,
				SingleTypeCollectionTestHsqlRefAsTable.class,

				TransientMixedTypesCollectionReplicationTest.class,

				TransientArrayReplicationTest.class,
				Db4oArrayReplicationTest.class,

				UuidLongPartGeneratorTest.class,
				ReplicationConfiguratorTest.class,
				RefAsTableReplicationConfiguratorTest.class,

				ReplicationTraversalTest.class,
				ReplicationFeatures.class,
		};
	}
}