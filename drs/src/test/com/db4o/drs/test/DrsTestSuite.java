/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import com.db4o.drs.test.hibernate.RoundRobinWithManyProviders;

import db4ounit.TestSuite;
import db4ounit.TestSuiteBuilder;

/**
 * @exclude
 */
public abstract class DrsTestSuite extends DrsTestCase implements
		TestSuiteBuilder {

	public TestSuite build() {
		return new DrsTestSuiteBuilder(a(), b(), testCases()).build();
	}

	protected abstract Class[] testCases() ;

	protected Class[] shared() {
		return new Class[] {
				// Simple
				//DO NOT run this - Hibernate does not have mappings InheritanceTest.class,
				TheSimplest.class, 
				ReplicationEventTest.class,
				ReplicationProviderTest.class,
				ReplicationAfterDeletionTest.class,
				SimpleArrayTest.class,
				SimpleParentChild.class,
				RoundRobinWithManyProviders.class,
//
//				// Collection
				MapTest.class, // TODO : Convert to .NET
				ArrayReplicationTest.class,// TODO : Convert to .NET
				ListTest.class, 
				Db4oListTest.class, 
				SingleTypeCollectionReplicationTest.class,
				MixedTypesCollectionReplicationTest.class,// TODO : Convert to .NET
//
//				// Complex
				R0to4Runner.class, 	
				ReplicationFeaturesMain.class,
//
//				// General
				//CollectionHandlerImplTest.class,  
				//ReplicationTraversalTest.class,
				};
	}
}
