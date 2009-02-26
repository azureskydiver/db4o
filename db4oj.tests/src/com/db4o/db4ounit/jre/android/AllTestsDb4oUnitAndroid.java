/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre.android;

import com.db4o.db4ounit.jre5.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

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

			protected Db4oTestSuiteBuilder soloSuite(boolean independentConfig) {
		        return new Db4oTestSuiteBuilder(
	                new Db4oAndroid(Db4oFixtures.configSource(independentConfig)), testCases());			}
			
		};
		suite.runSolo();
	}


}
