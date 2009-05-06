/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


/**
 * @exclude
 */
public class PersistentIntegerArrayTestCase extends AbstractDb4oTestCase implements OptOutCS, OptOutDefragSolo {
	
	public static void main(String[] arguments) {
		new PersistentIntegerArrayTestCase().runSolo();
	}
	
	public void test() throws Exception{
		int[] original = new int[] {10,99,77};
		PersistentIntegerArray arr = new PersistentIntegerArray(original);
		arr.write(systemTrans());
		int id = arr.getID();
		reopen();
		arr = new PersistentIntegerArray(id);
		arr.read(systemTrans());
		int[] copy = arr.array();
		ArrayAssert.areEqual(original, copy);
	}
	
}
