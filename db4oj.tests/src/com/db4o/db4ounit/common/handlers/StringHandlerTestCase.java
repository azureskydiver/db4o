package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.handlers.*;
import com.db4o.inside.slots.Slot;

import db4ounit.*;
import db4ounit.extensions.*;

public class StringHandlerTestCase extends AbstractDb4oTestCase {

	public void testIndexMarshalling() {
		Buffer reader=new Buffer(2*Const4.INT_LENGTH);
		ObjectContainerBase stream=(ObjectContainerBase)db();
		StringHandler handler=new StringHandler(stream,stream.stringIO());
		final Slot original = new Slot(0xdb,0x40);
		handler.writeIndexEntry(reader,original);
		reader._offset=0;
		Slot retrieved = (Slot) handler.readIndexEntry(reader);
		Assert.areEqual(original._address, retrieved._address);
		Assert.areEqual(original._length, retrieved._length);
	}
	
}
