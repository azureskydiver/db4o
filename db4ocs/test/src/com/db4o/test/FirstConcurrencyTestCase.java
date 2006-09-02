/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.test;
import db4ounit.TestCase;

public class FirstConcurrencyTestCase implements TestCase{
	public void concCase1() {
		System.out.println("testConcurrencyCase1");
	}

	public void concCase2() {
		System.out.println("testConcurrencyCase2");
	}

	public void concCase3(int threadId) {
		System.out.println("testConcurrencyCase3, thread sequence = "
				+ threadId);
	}
	
	public void concCase4() throws Exception {
		throw new Exception("testConcurrencyCase4 failed");
	}

	public void checkCase1() {
		System.out.println("checkConcurrencyCase2");
	}
	public void checkCase3() {
		System.out.println("checkConcurrencyCase3");
	}

	
}
