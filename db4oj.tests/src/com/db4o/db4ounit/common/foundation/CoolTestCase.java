/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class CoolTestCase implements TestCase {

	public void testLoopWithTimeoutReturnsWhenBlockIsFalse() {
		
		StopWatch watch = new AutoStopWatch();		
		Cool.loopWithTimeout(500, new ConditionalBlock() {
			public boolean run() {
				return false;
			}
		});
		Assert.isTrue(watch.peek() < 500);
	}
	
	public void testLoopWithTimeoutReturnsAfterTimeout() {
		StopWatch watch = new AutoStopWatch();		
		Cool.loopWithTimeout(500, new ConditionalBlock() {
			public boolean run() {
				return true;
			}
		});
		watch.stop();
		Assert.isTrue(watch.elapsed() >= 500 && watch.elapsed() <= 600);
	}
	
}
