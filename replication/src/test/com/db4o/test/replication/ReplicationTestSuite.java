package com.db4o.test.replication;

import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.*;
import com.db4o.test.replication.db4o.hibernate.*;
import com.db4o.test.replication.hibernate.*;
import com.db4o.test.replication.provider.TransientReplicationProviderTest;
import com.db4o.test.replication.transients.*;

public class ReplicationTestSuite extends TestSuite {

	public Class[] tests() {
		System.err.println("Db4oReplicationProvider.getReference(obj).version() must return the latest version of all collections held by obj because collections are being treated as 2nd class objects (like arrays) for hibernate replication purposes.");
		System.err.println("Overcome absence of constructor in VMs other than Sun's.");
        
		return new Class[]{
                //UuidLongPartGeneratorTest.class,
				TransientReplicationProviderTest.class,
				Db4oReplicationProviderTest.class,
				HibernateReplicationProviderTest.class,

				TransientSimpleParentChild.class,
				Db4oSimpleParentChild.class,
				HibernateSimpleParentChild.class,
				Db4oHibernateSimpleParentChild.class,

				TransientReplicationFeaturesMain.class,
				Db4oReplicationFeaturesMain.class,
				HibernateReplicationFeaturesMain.class,
                
                TransientListTest.class,
                Db4oListTest.class,
                // HibernateListTest.class,
                
                TransientSimpleArrayTest.class,
                Db4oSimpleArrayTest.class,
                HibernateSimpleArrayTest.class,
                // Db4oHibernateSimpleArrayTest.class,

				ReplicationConfiguratorTest.class,

				// TransientSingleTypeCollectionReplicationTest.class,
				// HibernateSingleTypeCollectionReplicationTest.class,

				// TransientMixedTypesCollectionReplicationTest.class,

				TransientR0to4Runner.class,
				Db4oR0to4Runner.class,
                HibernateR0to4Runner.class,
                // Db4oHibernateR0to4Runner.class,

				ReplicationTraversalTest.class,

				TransientArrayReplicationTest.class,
				Db4oArrayReplicationTest.class,
				//HibernateArrayReplicationTest.class,

				ReplicationFeatures.class,
		};
	}
}
