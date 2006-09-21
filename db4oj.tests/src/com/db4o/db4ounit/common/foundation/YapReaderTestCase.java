package com.db4o.db4ounit.common.foundation;

import com.db4o.*;

import db4ounit.*;

public class YapReaderTestCase implements TestCase {

	private static final int READERLENGTH = 64;

	public void testCopy() {
		YapReader from=new YapReader(READERLENGTH);
		for(int i=0;i<READERLENGTH;i++) {
			from.append((byte)i);
		}
		YapReader to=new YapReader(READERLENGTH-1);
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
