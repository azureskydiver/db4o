/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import com.db4o.drs.test.*;
import com.db4o.drs.versant.jdo.reflect.*;
import com.db4o.foundation.*;
import com.db4o.reflect.*;

import db4ounit.*;

public class AllVodDrsIntegrationTests implements TestSuiteBuilder {

	Reflector reflector = new JdoReflector(getClass().getClassLoader());
	
	public Iterator4 iterator() {
		
//		return new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-drs-a"),
//				new Db4oDrsFixture("db4o-drs-b"), VodDrsSuite.class).iterator();

//		 new DrsTestSuiteBuilder(new VodDrsFixture("vod-drs-a"),
//				new Db4oDrsFixture("db4o-drs-b", reflector), VodDrsSuite.class, reflector).iterator();
		
		 return new DrsTestSuiteBuilder( 
				 new VodDrsFixture("vod-drs-a"), 
				 new Db4oDrsFixture("db4o-drs-b", reflector), 
				 VodDrsSuite.class, 
				 reflector
		 ).iterator();

		 
//		 return new DrsTestSuiteBuilder(
//				 new Db4oDrsFixture("db4o-drs-a", reflector), 
//				 new VodDrsFixture("vod-drs-b"), 
//				 VodDrsSuite.class, 
//				 reflector
//		 ).iterator();

		
//		return new DrsTestSuiteBuilder(new VodDrsFixture("vod-drs-a"),
//				new VodDrsFixture("vod-drs-b"), VodDrsSuite.class).iterator();
		
	}
	
	public static class VodDrsSuite extends DrsTestSuite {
		
		
		
		@SuppressWarnings("unchecked")
		@Override
		protected Class[] testCases() {
			
			
			if (true) {
				
				// This is the one we are heading for for now.
				return new Class[] {
						UuidConversionTestCase.class,
 						TheSimplest.class,
						ReplicationProviderTest.class,
//						ReplicationEventTest.class,
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
