/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.*;
import com.db4o.internal.handlers.StringHandler;
import com.db4o.internal.slots.Slot;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class StringHandlerTestCase extends AbstractDb4oTestCase {

	public void testIndexMarshalling() {
		Buffer reader=new Buffer(2*Const4.INT_LENGTH);
		ObjectContainerBase stream=(ObjectContainerBase)db();
		StringHandler handler=new StringHandler(stream,stream.stringIO());
		final Slot original = new Slot(0xdb,0x40);
		handler.writeIndexEntry(reader,original);
		reader._offset=0;
		Slot retrieved = (Slot) handler.readIndexEntry(reader);
		Assert.areEqual(original.address(), retrieved.address());
		Assert.areEqual(original.length(), retrieved.length());
	}
	
}
