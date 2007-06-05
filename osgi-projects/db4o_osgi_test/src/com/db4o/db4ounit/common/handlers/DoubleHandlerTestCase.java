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
import com.db4o.internal.handlers.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

/**
 * @exclude
 */
public class DoubleHandlerTestCase extends AbstractDb4oTestCase {
	
	private TypeHandler4 _handler;
	
	protected void db4oSetupBeforeStore() throws Exception {
		_handler = new DoubleHandler(stream());
	}
	
	public void testMarshalling() {
		final Double expected = new Double(1.1);
		
		Buffer buffer = new Buffer(_handler.linkLength());		
		_handler.writeIndexEntry(buffer, expected);
		
		buffer.seek(0);
		final Object actual = _handler.readIndexEntry(buffer);
		Assert.areEqual(expected, actual);
	}

	public void testComparison() {		
		assertComparison(0, 1.1, 1.1);
		assertComparison(1, 1.0, 1.1);
		assertComparison(-1, 1.1, 0.5);
	}

	private void assertComparison(final int expected, final double prepareWith, final double compareTo) {
		_handler.prepareComparison(new Double(prepareWith));		
		final Double doubleCompareTo = new Double(compareTo);
		Assert.areEqual(expected, _handler.compareTo(doubleCompareTo));
		switch (expected) {
		case 0:
			Assert.isTrue(_handler.isEqual(doubleCompareTo));
			Assert.isFalse(_handler.isGreater(doubleCompareTo));
			Assert.isFalse(_handler.isSmaller(doubleCompareTo));
			break;
		case 1:
			Assert.isFalse(_handler.isEqual(doubleCompareTo));
			Assert.isTrue(_handler.isGreater(doubleCompareTo));
			Assert.isFalse(_handler.isSmaller(doubleCompareTo));
			break;
		case -1:
			Assert.isFalse(_handler.isEqual(doubleCompareTo));
			Assert.isFalse(_handler.isGreater(doubleCompareTo));
			Assert.isTrue(_handler.isSmaller(doubleCompareTo));
			break;
		}
		
	}
}
