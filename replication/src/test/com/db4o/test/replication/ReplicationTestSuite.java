package com.db4o.test.replication;

import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.Db4oArrayReplicationTest;
import com.db4o.test.replication.db4o.Db4oListTest;
import com.db4o.test.replication.db4o.Db4oR0to4Runner;
import com.db4o.test.replication.db4o.Db4oReplicationAfterDeletionTest;
import com.db4o.test.replication.db4o.Db4oReplicationFeaturesMain;
import com.db4o.test.replication.db4o.Db4oReplicationProviderTest;
import com.db4o.test.replication.db4o.Db4oSimpleArrayTest;
import com.db4o.test.replication.db4o.Db4oSimpleParentChild;
import com.db4o.test.replication.hibernate.HibernateListTest;
import com.db4o.test.replication.hibernate.HibernateR0to4Runner;
import com.db4o.test.replication.hibernate.HibernateReplicationAfterDeletionTest;
import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;
import com.db4o.test.replication.hibernate.HibernateReplicationProviderTest;
import com.db4o.test.replication.hibernate.HibernateSimpleArrayTest;
import com.db4o.test.replication.hibernate.HibernateSimpleParentChild;
import com.db4o.test.replication.hibernate.HibernateSingleTypeCollectionReplicationTest;
import com.db4o.test.replication.hibernate.ReplicationConfiguratorTest;
import com.db4o.test.replication.hibernate.UuidLongPartGeneratorTest;
import com.db4o.test.replication.provider.TransientReplicationProviderTest;
import com.db4o.test.replication.transients.TransientArrayReplicationTest;
import com.db4o.test.replication.transients.TransientListTest;
import com.db4o.test.replication.transients.TransientMixedTypesCollectionReplicationTest;
import com.db4o.test.replication.transients.TransientR0to4Runner;
import com.db4o.test.replication.transients.TransientReplicationFeaturesMain;
import com.db4o.test.replication.transients.TransientSimpleArrayTest;
import com.db4o.test.replication.transients.TransientSimpleParentChild;
import com.db4o.test.replication.transients.TransientSingleTypeCollectionReplicationTest;

public class ReplicationTestSuite extends TestSuite {

	public Class[] tests() {
		System.err.println("Db4oReplicationProvider.getReference(obj).version() must return the latest version of all collections held by obj because collections are being treated as 2nd class objects (like arrays) for hibernate replication purposes.");
		System.err.println("Overcome absence of constructor in VMs other than Sun's.");
		System.err.println("Uncommenting the FIXME Db4oReplicationProvider to debug Db4oReplicationProvider");
		return new Class[]{
				HibernateReplicationAfterDeletionTest.class,
				Db4oReplicationAfterDeletionTest.class,

				TransientReplicationProviderTest.class,
				Db4oReplicationProviderTest.class,
				HibernateReplicationProviderTest.class,

				TransientR0to4Runner.class,
				Db4oR0to4Runner.class,
				HibernateR0to4Runner.class,
				//Db4oHibernateR0to4Runner.class,

				TransientSimpleParentChild.class,
				Db4oSimpleParentChild.class,
				HibernateSimpleParentChild.class,
				//Db4oHibernateSimpleParentChild.class,

				TransientListTest.class,
				Db4oListTest.class,
				HibernateListTest.class,

				TransientSimpleArrayTest.class,
				Db4oSimpleArrayTest.class,
				HibernateSimpleArrayTest.class,
				//Db4oHibernateSimpleArrayTest.class,

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