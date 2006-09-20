package com.db4o.drs.test;

import com.db4o.test.AllTests;
import com.db4o.test.TestSuite;
import com.db4o.Db4o;

public class OtherTests extends AllTests {

	public static void main(String[] args) {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
		
		new OtherTests().run();
	}

	protected void addTestSuites(TestSuite suites) {
		suites.add(new OtherTestSuite());
	}
}

class OtherTestSuite extends TestSuite {
	public Class[] tests() {
		return new Class[]{
				ObjectVersionTest.class,
		};
	}
}
