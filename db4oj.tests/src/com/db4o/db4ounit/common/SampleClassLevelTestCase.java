package com.db4o.db4ounit.common;

import db4ounit.*;
import db4ounit.extensions.*;

public class SampleClassLevelTestCase extends AbstractDb4oTestCase implements ClassLevelFixtureTest {

	public static void main(String[] args) {
		new SampleClassLevelTestCase().runSolo();
	}
	
	public static void classSetUp() {
		System.out.println("setup");
	}

	public static void classTearDown() {
		System.out.println("teardown");
	}
	
	public void test() {
		System.out.println(db());
	}

}
