package com.db4o.test;

import db4ounit.TestRunner;
import db4ounit.extensions.Db4oTestSuiteBuilder;
import db4ounit.extensions.fixtures.Db4oClientServer;
import ClientServerTestCase;

public class SampleTest extends ClientServerTestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestRunner(
                new Db4oTestSuiteBuilder(
                        new Db4oClientServer("Db4oClientServer.yap",0xdb40),
                        SampleTest.class)).run();

	}

	public void testGetByUUID() {
		
	}
}
