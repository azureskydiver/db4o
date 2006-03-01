package com.db4o.test.replication;

import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.Db4oArrayReplicationTest;
import com.db4o.test.replication.db4o.Db4oListTest;
import com.db4o.test.replication.db4o.Db4oMapTest;
import com.db4o.test.replication.db4o.Db4oR0to4Runner;
import com.db4o.test.replication.db4o.Db4oReplicationAfterDeletionTest;
import com.db4o.test.replication.db4o.Db4oReplicationFeaturesMain;
import com.db4o.test.replication.db4o.Db4oReplicationProviderTest;
import com.db4o.test.replication.db4o.Db4oSimpleArrayTest;
import com.db4o.test.replication.db4o.Db4oSimpleParentChild;
import com.db4o.test.replication.db4o.hibernate.Db4oHibernateListTest;
import com.db4o.test.replication.db4o.hibernate.Db4oHibernateSimpleArrayTest;
import com.db4o.test.replication.db4o.hibernate.Db4oHibernateSimpleParentChild;
import com.db4o.test.replication.db4o.hibernate.Db4oHsqlR0to4Runner;
import com.db4o.test.replication.db4o.hibernate.HibernateDb4oListTest;
import com.db4o.test.replication.hibernate.HibernateListTest;
import com.db4o.test.replication.hibernate.HibernateMapTest;
import com.db4o.test.replication.hibernate.HibernateReplicationAfterDeletionTest;
import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;
import com.db4o.test.replication.hibernate.HibernateReplicationProviderTest;
import com.db4o.test.replication.hibernate.HibernateSimpleArrayTest;
import com.db4o.test.replication.hibernate.HibernateSimpleParentChild;
import com.db4o.test.replication.hibernate.HibernateSingleTypeCollectionReplicationTest;
import com.db4o.test.replication.hibernate.ReplicationConfiguratorTest;
import com.db4o.test.replication.hibernate.UuidLongPartGeneratorTest;
import com.db4o.test.replication.hibernate.hsql.HsqlMetaDataTablesCreatorTest;
import com.db4o.test.replication.hibernate.hsql.HsqlR0to4Runner;
import com.db4o.test.replication.provider.TransientReplicationProviderTest;
import com.db4o.test.replication.transients.TransientArrayReplicationTest;
import com.db4o.test.replication.transients.TransientListTest;
import com.db4o.test.replication.transients.TransientMapTest;
import com.db4o.test.replication.transients.TransientMixedTypesCollectionReplicationTest;
import com.db4o.test.replication.transients.TransientR0to4Runner;
import com.db4o.test.replication.transients.TransientReplicationFeaturesMain;
import com.db4o.test.replication.transients.TransientSimpleArrayTest;
import com.db4o.test.replication.transients.TransientSimpleParentChild;
import com.db4o.test.replication.transients.TransientSingleTypeCollectionReplicationTest;

public class ReplicationTestSuite extends TestSuite {

	public Class[] tests() {

		// System.err.println("Db4oReplicationProvider.getReference(obj).version() must return the latest version of all collections held by obj because collections are being treated as 2nd class objects (like arrays) for hibernate replication purposes.");
		// System.err.println("Overcome absence of constructor in VMs other than Sun's.");


		return new Class[]{
				HsqlMetaDataTablesCreatorTest.class,
				HibernateReplicationAfterDeletionTest.class,
				Db4oReplicationAfterDeletionTest.class,

				TransientReplicationProviderTest.class,
				Db4oReplicationProviderTest.class,
				HibernateReplicationProviderTest.class,

				TransientR0to4Runner.class,
				Db4oR0to4Runner.class,
				HsqlR0to4Runner.class,
				Db4oHsqlR0to4Runner.class,

				TransientSimpleParentChild.class,
				Db4oSimpleParentChild.class,
				HibernateSimpleParentChild.class,
				Db4oHibernateSimpleParentChild.class,

				TransientListTest.class,
				Db4oListTest.class,
				HibernateListTest.class,
				HibernateDb4oListTest.class,
				Db4oHibernateListTest.class,

				TransientMapTest.class,
				Db4oMapTest.class,
				HibernateMapTest.class,

				TransientSimpleArrayTest.class,
				Db4oSimpleArrayTest.class,
				HibernateSimpleArrayTest.class,
				Db4oHibernateSimpleArrayTest.class,

				TransientSingleTypeCollectionReplicationTest.class,
				HibernateSingleTypeCollectionReplicationTest.class,
				TransientMixedTypesCollectionReplicationTest.class,

				TransientArrayReplicationTest.class,
				Db4oArrayReplicationTest.class,

				UuidLongPartGeneratorTest.class,
				ReplicationConfiguratorTest.class,
				ReplicationTraversalTest.class,
				ReplicationFeatures.class,

				TransientReplicationFeaturesMain.class,
				Db4oReplicationFeaturesMain.class,
				HibernateReplicationFeaturesMain.class,
		};
	}
}