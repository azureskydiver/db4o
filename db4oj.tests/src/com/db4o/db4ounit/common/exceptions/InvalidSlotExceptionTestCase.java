/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class InvalidSlotExceptionTestCase extends AbstractDb4oTestCase {

	private static final int MAX = 100000;

	public static void main(String[] args) {
		new InvalidSlotExceptionTestCase().runAll();
	}
	
	public void testInvalidSlotException() throws Exception {
		Assert.expect(InvalidIDException.class, InvalidSlotException.class, new CodeBlock(){
			public void run() throws Throwable {
				/*Object byID = */db().getByID(1);		
			}
		});
		
	}
	
	public void _testTimes() throws Exception {
		long ids[] = new long[MAX];
		for (int i = 0; i < MAX; i++) {
			Object o = complexObject();
			db().store(o);
			ids[i] = db().ext().getID(o);
		}
		reopen();
		for (int i = 0; i < MAX; i++) {
			db().ext().getByID(ids[i]);
		}
	}

	public static class A{
		A _a;
		public A(A a) {
			this._a = a;
		}
	}
	private Object complexObject() {
		return new A(new A(new A(null)));
	}
	
	

}
