/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.drs.test;

import db4ounit.*;

/**
 * @sharpen.partial
 */
public class Db4oTests extends DrsTestSuite {
	public static int main(String[] args) {
		//if (true) return new Db4oTests().runDb4oDb4o();
		
		int failureCount = new Db4oTests().runDb4oDb4o();
		failureCount += new Db4oTests().rundb4oCS();
		//new Db4oTests().runCSdb4o();
		failureCount += new Db4oTests().runCSCS();
		//new Db4oTests().runDb4oDb4o();
		return failureCount;
	}

	public int runDb4oDb4o() {
		return new ConsoleTestRunner(new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-a"),
				new Db4oDrsFixture("db4o-b"), getClass())).run();
	}

	public int runCSCS() {
		return new ConsoleTestRunner(new DrsTestSuiteBuilder(new Db4oClientServerDrsFixture(
				"db4o-cs-a", 0xdb40), new Db4oClientServerDrsFixture(
				"db4o-cs-b", 4455), getClass())).run();
	}

	public int rundb4oCS() {
		return new ConsoleTestRunner(new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 4455), getClass()))
				.run();
	}

	public void runCSdb4o() {
		new ConsoleTestRunner(new DrsTestSuiteBuilder(new Db4oClientServerDrsFixture(
				"db4o-cs-a", 4455), new Db4oDrsFixture("db4o-b"), getClass()))
				.run();
	}
	
	/**
	 * @sharpen.ignore
	 */
	private Class[] platformSpecificTestCases() {
		return new Class[0];
	}
	
	protected Class[] specificTestCases() {
		return concat(
			platformSpecificTestCases(), 
			new Class[] {
				com.db4o.drs.test.ArrayTestSuite.class,
				com.db4o.drs.test.CustomArrayListTestCase.class,
				com.db4o.drs.test.DateReplicationTestCase.class,
				com.db4o.drs.test.dotnet.StructTestCase.class,
				com.db4o.drs.test.DeepListGraphTestCase.class,
				com.db4o.drs.test.UntypedFieldTestCase.class,
				com.db4o.drs.test.db4o.PartialCollectionReplicationTestCase.class,
				TheSimplestWithCallConstructors.class,
			});
	}
}
