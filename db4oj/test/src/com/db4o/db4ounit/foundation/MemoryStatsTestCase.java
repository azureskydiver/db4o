/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.foundation;

import db4ounit.Assert;


public class MemoryStatsTestCase implements db4ounit.TestCase {
	
	public void testUsedMemory() {
		long memory1 = usedMemory();
		
		byte[] buffer = new byte[1024*64];
		Assert.isNotNull(buffer); // just to remove 'variable is not read' warning
		
		long memory2 = usedMemory();
		
		Assert.isTrue(memory2 > memory1);
		
		buffer = null;
		
		// very VM and runtime dependent but let's see
		// how long we can get away with it
		long memory3 = usedMemory();
		Assert.isTrue(memory3 < memory2);
	}

	private long usedMemory() {
		return com.db4o.foundation.MemoryStats.usedMemory();
	}

}
