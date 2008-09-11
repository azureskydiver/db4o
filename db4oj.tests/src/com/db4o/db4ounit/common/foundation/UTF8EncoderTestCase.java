/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import java.io.*;

import com.db4o.foundation.*;

import db4ounit.*;

/**
 * @exclude
 */
public class UTF8EncoderTestCase implements TestLifeCycle{

	public void setUp() throws Exception {
		
	}

	public void tearDown() throws Exception {
		
	}
	
	public void testEncodeDecode() throws IOException{
		String original = "ABCZabcz?$@#.,;:";
		UTF8Encoder encoder = new UTF8Encoder();
		byte[] bytes = encoder.encode(original);
		String decoded = encoder.decode(bytes, 0, bytes.length);
		Assert.areEqual(original, decoded);
	}

}
