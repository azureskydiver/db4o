/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;


public class TimeTest {

	private static final int ITERATIONS = 10000;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TimeTest().runTests();
	}
	
	public void runTests() {
		testNanoTimeResolution();
		testNanoTimeDifference();
		testCurrentTimeMillisResolution();
		testCurrentTimeMillisDifference();
	}

	public void testNanoTimeResolution() {
		System.out.println();
		printLine();
		System.out.println("Testing System.nanoTime() resolution");
		printLine();
		
		for (int i = 0; i < ITERATIONS; i++) {
			System.out.println(System.nanoTime());
		}
	}

	public void testNanoTimeDifference() {
		System.out.println();
		printLine();
		System.out.println("Testing difference between 2 calls to System.nanoTime()");
		printLine();
		
		long start, stop;
		for (int i = 0; i < ITERATIONS; i++) {
			start = System.nanoTime();
			stop = System.nanoTime();
			System.out.println(stop - start);
		}
	}
	
	public void testCurrentTimeMillisResolution() {
		System.out.println();
		printLine();
		System.out.println("Testing System.currentTimeMillis() resolution");
		printLine();
		
		for (int i = 0; i < ITERATIONS; i++) {
			System.out.println(System.currentTimeMillis());
		}
	}
	
	public void testCurrentTimeMillisDifference() {
		System.out.println();
		printLine();
		System.out.println("Testing difference between 2 calls to System.currentTimeMillis()");
		printLine();
		
		long start, stop;
		for (int i = 0; i < ITERATIONS; i++) {
			start = System.currentTimeMillis();
			stop = System.currentTimeMillis();
			System.out.println(stop - start);
		}
	}

	private void printLine() {
		System.out.println("------------------------------------------------------------");
	}
	
}
