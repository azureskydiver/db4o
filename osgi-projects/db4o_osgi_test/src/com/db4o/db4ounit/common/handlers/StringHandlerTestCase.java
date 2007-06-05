/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
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
