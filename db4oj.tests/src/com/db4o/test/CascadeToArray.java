/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;


import com.db4o.*;
import com.db4o.foundation.*;

public class CascadeToArray {

	public Object[] objects;

	public void configure() {
		Db4o.configure().objectClass(this).cascadeOnUpdate(true);
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
	}

	public void store() {
		Test.deleteAllInstances(this);
		Test.deleteAllInstances(new Atom());
		CascadeToArray cta = new CascadeToArray();
		cta.objects = new Object[] {new Atom("stored1"), new Atom(new Atom("storedChild1"), "stored2")};
		Test.store(cta);
		Test.commit();
	}

	public void test() {
		Test.forEach(this, new Visitor4() {
			public void visit(Object obj) {
				CascadeToArray cta = (CascadeToArray) obj;
				for (int i = 0; i < cta.objects.length; i++) {
					Atom atom = (Atom) cta.objects[i];
					atom.name = "updated";
					if(atom.child != null){
						// This one should NOT cascade
						atom.child.name = "updated";
					}
				}
				Test.store(cta);
			}
		});
		Test.reOpen();
		
		Test.forEach(this, new Visitor4() {
			public void visit(Object obj) {
				CascadeToArray cta = (CascadeToArray) obj;
				for (int i = 0; i < cta.objects.length; i++) {
					Atom atom = (Atom) cta.objects[i];
					Test.ensure(atom.name.equals("updated"));
					if(atom.child != null){
						Test.ensure( ! atom.child.name.equals("updated"));
					}
				}
			}
		});
		
		
		// Cascade-On-Delete Test: We only want one Atom to remain.
		Test.commit();
		Test.reOpen();
		Test.deleteAllInstances(this);
		// Test.commit();
		Test.ensureOccurrences(new Atom(), 1);
	}
}
