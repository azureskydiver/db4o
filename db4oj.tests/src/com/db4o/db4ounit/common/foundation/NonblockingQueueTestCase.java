/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class NonblockingQueueTestCase extends Queue4TestCaseBase {

	public void testIterator() {
		Queue4 queue=new NonblockingQueue();
		String[] data={"a","b","c","d"};
		for (int idx = 0; idx < data.length; idx++) {
			assertIterator(queue, data, idx);
			queue.add(data[idx]);
			assertIterator(queue, data, idx+1);
		}
	}
	
	public void testNext() {
		Queue4 queue = new BlockingQueue();
		String[] data = { "a", "b", "c", "d" };
		queue.add(data[0]);
		Assert.areSame(data[0], queue.next());
		queue.add(data[1]);
		queue.add(data[2]);
		Assert.areSame(data[1], queue.next());
		Assert.areSame(data[2], queue.next());
	}
	
}
