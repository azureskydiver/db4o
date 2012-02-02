/* Copyright (C) 2004 - 2011  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant.timestamp;

import com.db4o.drs.versant.timestamp.*;

import db4ounit.*;

public class TimestampGeneratorTestCase implements TestCase{
	
	public void test(){
		TimestampGenerator timestampGenerator = new TimestampGenerator();
		long timestamp = timestampGenerator.generate();
		Assert.isGreater(0, timestamp);
	}

}
