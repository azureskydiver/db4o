package com.db4o.cs.performance;

/**
 * User: treeder
 * Date: Dec 4, 2006
 * Time: 2:20:51 PM
 */
public class ConcClientServerPerformanceTest extends ClientServerPerformanceTest {
	public static int THREADS = 8;
	
	public static void main(String[] args) {
		for(int i = 0; i < THREADS; i++){
			Thread t = new Thread(new ClientServerPerformanceTest());
			t.start();
		}
	}
}
