/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import com.db4o.drs.test.*;
import com.db4o.drs.test.regression.*;
import com.db4o.drs.versant.jdo.reflect.*;
import com.db4o.foundation.*;
import com.db4o.reflect.*;

import db4ounit.*;

public class AllVodDrsIntegrationTests implements TestSuiteBuilder {

	Reflector reflector = new JdoReflector(getClass().getClassLoader());
	
	public Iterator4 iterator() {
		
//		 return new DrsTestSuiteBuilder( 
//				 new VodDrsFixture("vod-drs-a"), 
//				 new Db4oDrsFixture("db4o-drs-b", reflector), 
//				 VodDrsSuite.class, 
//				 reflector
//		 ).iterator();

		 
		 return new DrsTestSuiteBuilder(
				 new Db4oDrsFixture("db4o-drs-a", reflector), 
				 new VodDrsFixture("vod-drs-b"), 
				 VodDrsSuite.class, 
				 reflector
		 ).iterator();
		
	}
	
	public static class VodDrsSuite extends DrsTestSuite {
		
		
		
		@SuppressWarnings("unchecked")
		@Override
		protected Class[] testCases() {
			
			
			if (true) {
				
				// This is the one we are heading for for now.
				return new Class[] {
						
						/*  Passing  Tests */
						
						com.db4o.drs.test.foundation.AllTests.class,
						com.db4o.drs.test.objectid.AllTests.class,
						ReplicationProviderTest.class,
 						TheSimplest.class,
						UuidConversionTestCase.class,
	
						
						/*  Failing Tests */ 
//						ReplicationEventTest.class,
						
						
						/* Tests that haven't been checked yet */
						
						// Simple
//						ReplicationAfterDeletionTest.class,
//						SimpleArrayTest.class,
//						SimpleParentChild.class,
//						ByteArrayTest.class,
//						
//						// Collection
//						ComplexListTestCase.class,
//						ListTest.class, 
//
//						// Complex
//						R0to4Runner.class, 	
//						ReplicationFeaturesMain.class,
//
//						// General
//						CollectionHandlerImplTest.class,  
//						ReplicationTraversalTest.class,
//				
//						MapTest.class,
//						ArrayReplicationTest.class,
//						SingleTypeCollectionReplicationTest.class,
//						MixedTypesCollectionReplicationTest.class,
//						TransparentActivationTestCase.class,
//		                
//		                //regression
//		                DRS42Test.class,
//		                
//		                SameHashCodeTestCase.class,

						
						
				};
			}
			
			return super.testCases();
		}


		@Override
		protected Class[] specificTestCases() {
			return null;
		}

	}

}
