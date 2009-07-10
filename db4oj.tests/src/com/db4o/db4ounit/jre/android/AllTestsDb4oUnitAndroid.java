/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre.android;

import com.db4o.db4ounit.jre5.*;

import db4ounit.extensions.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore
public class AllTestsDb4oUnitAndroid extends AllTestsDb4oUnitJdk5{
	
	public static void main(String[] args) {
		Db4oTestSuite suite=new Db4oTestSuite() {
			protected Class[] testCases() {
				return new Class[] {
					AllTestsDb4oUnitAndroid.class,
				};
			}

			protected Db4oTestSuiteBuilder soloSuite() {
		        return new Db4oTestSuiteBuilder(
	                new Db4oAndroid(), testCases());			}
			
		};
		suite.runSolo();
	}


}
