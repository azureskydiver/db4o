/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import com.db4o.drs.test.*;
import com.db4o.drs.versant.jdo.reflect.*;
import com.db4o.foundation.*;
import com.db4o.reflect.*;

import db4ounit.*;

public class VodDrsTestSuiteBuilder implements TestSuiteBuilder {

	Reflector reflector = new JdoReflector(getClass().getClassLoader());
	
	public static void main(String[] args) {
		new ConsoleTestRunner(new VodDrsTestSuiteBuilder()).run();
	}

	public Iterator4 iterator() {
		
		return Iterators.concat(

			new DrsTestSuiteBuilder(
				new VodDrsFixture("vod-drs-a"), 
				new Db4oDrsFixture("db4o-drs-b", reflector), 
				VodDrsTestSuite.class, 
				reflector),
				
			new DrsTestSuiteBuilder(
				new Db4oDrsFixture("db4o-drs-a", reflector),
				new VodDrsFixture("vod-drs-b"), 
				VodDrsTestSuite.class, 
				reflector),

			new VodStandaloneTests()
			
		).iterator();
	}

}
