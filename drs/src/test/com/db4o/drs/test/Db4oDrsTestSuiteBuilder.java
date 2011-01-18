package com.db4o.drs.test;

import com.db4o.foundation.*;

import db4ounit.*;

public class Db4oDrsTestSuiteBuilder implements TestSuiteBuilder {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(new Db4oDrsTestSuiteBuilder()).run();
	}
	
	public Iterator4 iterator() {
		
		if(false)
			return new DrsTestSuiteBuilder(
				new Db4oDrsFixture("db4o-a"),
				new Db4oDrsFixture("db4o-b"),
				Db4oDrsTestSuite.class).iterator();
		
		if(false)
			return new DrsTestSuiteBuilder(
					new Db4oClientServerDrsFixture("db4o-cs-a", 0xdb40), 
					new Db4oClientServerDrsFixture("db4o-cs-b", 4455),
					Db4oDrsTestSuite.class).iterator();
		
		
		return Iterators.concat(
			Iterators.concat(
				new DrsTestSuiteBuilder(
					new Db4oDrsFixture("db4o-a"),
					new Db4oDrsFixture("db4o-b"),
					Db4oDrsTestSuite.class).iterator(),
				new DrsTestSuiteBuilder(
					new Db4oClientServerDrsFixture("db4o-cs-a", 0xdb40), 
					new Db4oClientServerDrsFixture("db4o-cs-b", 4455),
					Db4oDrsTestSuite.class).iterator()),
			Iterators.concat(
				new DrsTestSuiteBuilder(
					new Db4oDrsFixture("db4o-a"),
					new Db4oClientServerDrsFixture("db4o-cs-b", 4455),
					Db4oDrsTestSuite.class).iterator(),
			
				new DrsTestSuiteBuilder(
					new Db4oClientServerDrsFixture("db4o-cs-a", 4455), 
					new Db4oDrsFixture("db4o-b"), 
					Db4oDrsTestSuite.class).iterator()));
	}

}
