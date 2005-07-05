/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;

public class CascadeToHashMap {

	HashMap hm;

	public void configure() {
		Db4o.configure().objectClass(this).cascadeOnUpdate(true);
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
	}

	public void store() {
		Test.deleteAllInstances(this);
		Test.deleteAllInstances(new Atom());
		CascadeToHashMap cth = new CascadeToHashMap();
		cth.hm = new HashMap();
		cth.hm.put("key1", new Atom("stored1"));
		cth.hm.put("key2", new Atom(new Atom("storedChild1"), "stored2"));
		Test.store(cth);
	}

	public void test() {

		Test.forEach(this, new Visitor4() {
			public void visit(Object obj) {
				CascadeToHashMap cth = (CascadeToHashMap) obj;
				cth.hm.put("key1", new Atom("updated1"));
				Atom atom = (Atom)cth.hm.get("key2"); 
				atom.name = "updated2";
				Test.store(cth);
			}
		});
		Test.reOpen();
		
		Test.forEach(this, new Visitor4() {
			public void visit(Object obj) {
				CascadeToHashMap cth = (CascadeToHashMap) obj;
				Atom atom = (Atom)cth.hm.get("key1");
				Test.ensure(atom.name.equals("updated1"));
				atom = (Atom)cth.hm.get("key2");
				Test.ensure(atom.name.equals("updated2"));
			}
		});
		
		// Cascade-On-Delete Test: We only want one atom to remain.
		
		Test.reOpen();
		Test.deleteAllInstances(this);
		Test.ensureOccurrences(new Atom(), 1);
	}
}
