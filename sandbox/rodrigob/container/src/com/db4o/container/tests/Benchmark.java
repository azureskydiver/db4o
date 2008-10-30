package com.db4o.container.tests;

import com.db4o.container.*;
import com.db4o.container.tests.internal.*;

public class Benchmark {
	
	public static void main(String[] args) {
	    long t0 = time("raw object instantiation", new Runnable() {
			public void run() {
				new SimpleServiceImpl().toString();
			}
		});
	    
	    final Container container = ContainerFactory.newContainer();
	    long t1 = time("container service instantiation", new Runnable() {
			public void run() {
				container.produce(SimpleService.class).toString();
			}
		});
	    System.out.println("Overhead is " + (((((float)t1)/t0)-1)*100) + "%");
    }

	private static long time(String label, Runnable block) {
		final long t0 = System.nanoTime();
		for (int i=0; i<2000000; ++i)
			block.run();
		final long elapsed = System.nanoTime() - t0;
		System.out.println(label + ": " + (elapsed/1000000) + "ms");
		return elapsed;
    }
}
