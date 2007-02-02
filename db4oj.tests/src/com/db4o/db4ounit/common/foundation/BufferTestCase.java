package com.db4o.db4ounit.common.foundation;

import com.db4o.inside.*;

import db4ounit.*;

public class BufferTestCase implements TestCase {

	private static final int READERLENGTH = 64;

	public void testCopy() {
		Buffer from=new Buffer(READERLENGTH);
		for(int i=0;i<READERLENGTH;i++) {
			from.append((byte)i);
		}
		Buffer to=new Buffer(READERLENGTH-1);
		from.copyTo(to,1,2,10);
		
		Assert.areEqual(0,to.readByte());
		Assert.areEqual(0,to.readByte());
		for(int i=1;i<=10;i++) {
			Assert.areEqual((byte)i,to.readByte());
		}
		for(int i=12;i<READERLENGTH-1;i++) {
			Assert.areEqual(0,to.readByte());
		}
	}
	
}
