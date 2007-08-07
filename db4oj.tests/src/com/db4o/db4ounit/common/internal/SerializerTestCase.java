/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SerializerTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
		new SerializerTestCase().runAll();
	}
	
	public void testMarshall() throws Exception {
		ReflectException e = new ReflectException(new NullPointerException());
		SerializedGraph marshalled = Serializer.marshall(stream().container(), e);
		Assert.isTrue(marshalled.length() > 0);
	}
}
