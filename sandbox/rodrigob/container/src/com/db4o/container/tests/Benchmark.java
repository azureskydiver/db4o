package com.db4o.container.tests;

import com.db4o.container.*;
import com.db4o.container.tests.internal.*;
import com.db4o.foundation.*;

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
		final AutoStopWatch stopWatch = new AutoStopWatch();
		for (int i=0; i<1000000; ++i)
			block.run();
		final long elapsed = stopWatch.peek();
		System.out.println(label + ": " + elapsed + "ms");
		return elapsed;
    }

}
