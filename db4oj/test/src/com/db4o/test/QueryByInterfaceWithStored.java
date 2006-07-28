/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;


public class QueryByInterfaceWithStored extends QueryByInterfaceBase {

	public void store() {
		Test.objectContainer().set(new Bar(0));
		Test.objectContainer().set(new Bar(1));
		Test.objectContainer().set(new Baz("A"));
	}
	
	public void XtestSODA() {
		assertSODA("A",1);
		assertSODA("B",0);
	}

	public void testEvaluation() {
		assertEvaluation("A",2);
		assertEvaluation("B",1);
	}
}
