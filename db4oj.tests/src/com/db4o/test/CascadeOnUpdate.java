/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;


import com.db4o.*;
import com.db4o.foundation.*;

public class CascadeOnUpdate {

	public Object child;

	public void configure() {
		Db4o.configure().objectClass(this).cascadeOnUpdate(true);
	}

	public void store() {
		Test.deleteAllInstances(this);
		Test.deleteAllInstances(new Atom());
		CascadeOnUpdate cou = new CascadeOnUpdate();
		cou.child = new Atom(new Atom("storedChild"), "stored");
		Test.store(cou);
		Test.commit();
	}

	public void test() {
		Test.forEach(this, new Visitor4() {
			public void visit(Object obj) {
				CascadeOnUpdate cou = (CascadeOnUpdate) obj;
				((Atom)cou.child).name = "updated";
				((Atom)cou.child).child.name = "updated";
				Test.store(cou);
			}
		});
		Test.reOpen();
		
		Test.forEach(this, new Visitor4() {
			public void visit(Object obj) {
				CascadeOnUpdate cou = (CascadeOnUpdate) obj;
				Atom atom = (Atom)cou.child;
				Test.ensure(atom.name.equals("updated"));
				Test.ensure( ! atom.child.name.equals("updated"));
			}
		});
	}
}
