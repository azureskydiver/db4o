/* Copyright (C) 2010 Versant Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.foundation;

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

	public static void executeAfter(String threadName, final long timeInMillis, final Runnable runnable) {
		
		Thread t = new Thread(threadName) {
			@Override
			public void run() {
				try {
					Thread.sleep(timeInMillis);
				} catch (InterruptedException e) {
					return;
				}
				runnable.run();
				
			};
		};
		t.setDaemon(true);
		t.start();
	}


}
