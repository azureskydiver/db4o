/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.defrag;

import com.db4o.db4ounit.common.defragment.*;

import db4ounit.extensions.*;


/**
 */
@decaf.Ignore
public class RunTestsDefrag extends AbstractDb4oDefragTestCase {
	
	@Override
	public Class testSuite() {
		return com.db4o.db4ounit.jre5.AllTestsDb4oUnitJdk5.class;
	}
	
	public static void main(String[] args) {
		Db4oTestSuite suite=new Db4oTestSuite() {
			protected Class[] testCases() {
				return new Class[] {
					RunTestsDefrag.class,
//					InvalidIDExceptionTestCase.class,
				};
			}

			protected Db4oTestSuiteBuilder soloSuite(boolean independentConfig) {
		        return new Db4oTestSuiteBuilder(
	                new Db4oDefragSolo(configSource(independentConfig)), testCases());			}
			
		};
		
		System.exit(suite.runSolo());
	}

}
