/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.timing;


public class TimeTest {

	private static final int ITERATIONS = 10000;
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		new TimeTest().runTests();
	}
	
	public void runTests() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		testNanoTimeResolution();
		testNanoTimeDifference();
		testCurrentTimeMillisResolution();
		testCurrentTimeMillisDifference();
	}

	public void testNanoTimeResolution() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		System.out.println();
		printLine();
		System.out.println("Testing System.nanoTime() resolution");
		printLine();
		
		NanoTiming timing = NanoTimingInstance.newInstance();
		for (int i = 0; i < ITERATIONS; i++) {
			System.out.println(timing.nanoTime());
		}
	}

	public void testNanoTimeDifference() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		System.out.println();
		printLine();
		System.out.println("Testing difference between 2 calls to System.nanoTime()");
		printLine();
		
		NanoTiming timing = NanoTimingInstance.newInstance();
		long start, stop;
		for (int i = 0; i < ITERATIONS; i++) {
			start = timing.nanoTime();
			stop = timing.nanoTime();
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
