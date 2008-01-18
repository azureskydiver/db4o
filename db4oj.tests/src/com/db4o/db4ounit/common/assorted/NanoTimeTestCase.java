/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import db4ounit.*;
import db4ounit.extensions.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;


public class NanoTimeTestCase extends AbstractDb4oTestCase {

	public void testNanoTimeAvailable() {
		try {
			Platform4.nanoTime();
		} catch (NotImplementedException nie) {			
			return;
		} catch (Exception e) {
			Assert.fail("Wrong exception type", e);
		}
	}
	
}
