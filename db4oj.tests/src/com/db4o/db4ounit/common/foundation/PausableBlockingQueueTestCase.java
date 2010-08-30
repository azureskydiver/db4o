/* Copyright (C) 2010 Versant Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.foundation;

import java.util.*;

import com.db4o.foundation.*;

import db4ounit.*;

public class PausableBlockingQueueTestCase extends Queue4TestCaseBase {
	
	public void testTimeoutNext() {
		
		PausableBlockingQueue4<Object> queue = new PausableBlockingQueue<Object>();
		
		Assert.isFalse(queue.isPaused());
		
		queue.pause();
		
		Assert.isTrue(queue.isPaused());
		
		Object obj = new Object();
		
		queue.add(obj);
		
		Assert.isTrue(queue.hasNext());
		
		Assert.isNull(queue.tryNext());
		
		queue.resume();
		
		Assert.areSame(obj, queue.next(50));
		
	}
	
	public void testStop() {
		
		final PausableBlockingQueue4<Object> queue = new PausableBlockingQueue<Object>();

		queue.pause();

		executeAfter("Pausable queue stopper", 200, new Runnable() {
			public void run() {
				queue.stop();
			}
		});
		
		Assert.expect(BlockingQueueStoppedException.class, new CodeBlock() {
			
			public void run() throws Throwable {
				queue.next();
			}
		});	
		
		
	}
	
	public void testDrainTo() throws InterruptedException {
		final PausableBlockingQueue4<Object> queue = new PausableBlockingQueue<Object>();

		queue.add(new Object());
		queue.add(new Object());
		
		queue.pause();
		
		final List<Object> list = Collections.synchronizedList(new ArrayList<Object>());
		
		Thread t = executeAfter("Pausable queue drainer", 0, new Runnable() {
			public void run() {
				queue.drainTo(list);
			}
		});
		
		Runtime4.sleepThrowsOnInterrupt(200);
		
		Assert.areEqual(0, list.size());
		Assert.isTrue(queue.hasNext());

		queue.resume();
		
		t.join();
		
		Assert.areEqual(2, list.size());
		Assert.isFalse(queue.hasNext());
	}


	public static Thread executeAfter(String threadName, final long timeInMillis, final Runnable runnable) {
		
		Thread t = new Thread() {
			@Override
			public void run() {
				if (timeInMillis > 0) {
					try {
						Thread.sleep(timeInMillis);
					} catch (InterruptedException e) {
						return;
					}
				}
				runnable.run();
				
			};
		};
		t.setName(threadName);
		t.setDaemon(true);
		t.start();
		
		return t;
	}


}
