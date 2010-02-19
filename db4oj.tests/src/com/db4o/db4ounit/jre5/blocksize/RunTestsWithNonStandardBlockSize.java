/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.blocksize;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

@decaf.Ignore
public class RunTestsWithNonStandardBlockSize implements TestSuiteBuilder {
	
	public Class testSuite() {
		return com.db4o.db4ounit.jre5.AllTestsDb4oUnitJdk5.class;
	}
	
	public static void main(String[] args) {
		Db4oTestSuite suite=new Db4oTestSuite() {
			protected Class[] testCases() {
				return new Class[] {
						RunTestsWithNonStandardBlockSize.class,
				};
			}

			protected Db4oTestSuiteBuilder soloSuite() {
		        return new Db4oTestSuiteBuilder(
	                new NonStandardBlockSizeFixture(), testCases());			}
			
		};
		
		System.exit(suite.runSolo());
	}

	public Iterator4 iterator() {
		return new Db4oTestSuiteBuilder(
				new NonStandardBlockSizeFixture(), testSuite()).iterator();
	}

}
