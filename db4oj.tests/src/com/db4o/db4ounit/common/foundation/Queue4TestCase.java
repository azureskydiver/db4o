/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class Queue4TestCase implements TestCase {

	public void testIterator() {
		Queue4 queue=new Queue4();
		String[] data={"a","b","c","d"};
		for (int idx = 0; idx < data.length; idx++) {
			assertIterator(queue, data, idx);
			queue.add(data[idx]);
			assertIterator(queue, data, idx+1);
		}
	}

	private void assertIterator(Queue4 queue, String[] data,int size) {
		Iterator4 iter=queue.iterator();
		for (int idx = 0; idx < size; idx++) {
			Assert.isTrue(iter.moveNext(),"should be able to move in iteration #"+idx+" of "+size);
			Assert.areEqual(data[idx],iter.current());
		}
		Assert.isFalse(iter.moveNext());
	}
	
}
