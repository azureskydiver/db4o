/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

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
				TheSimplest.class, 
				ReplicationEventTest.class,
				ReplicationProviderTest.class,
				ReplicationAfterDeletionTest.class,
				SimpleArrayTest.class,
				SimpleParentChild.class,
				ByteArrayTest.class,
				
				// Collection
				ListTest.class, 
				Db4oListTest.class, 

//				// Complex
				R0to4Runner.class, 	
				ReplicationFeaturesMain.class,

//				// General
				//CollectionHandlerImplTest.class,  
				//ReplicationTraversalTest.class,
		
				//TODO Convert to .NET
				//MapTest.class,
				//ArrayReplicationTest.class,
				//SingleTypeCollectionReplicationTest.class,
				//MixedTypesCollectionReplicationTest.class
		};
	}
}
