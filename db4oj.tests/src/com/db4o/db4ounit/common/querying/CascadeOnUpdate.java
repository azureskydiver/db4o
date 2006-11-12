/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.querying;

import com.db4o.config.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeOnUpdate extends AbstractDb4oTestCase {
	
	public static class Atom {
		
		public Atom child;
		public String name;
		
		public Atom(){
		}
		
		public Atom(Atom child){
			this.child = child;
		}
		
		public Atom(String name){
			this.name = name;
		}
		
		public Atom(Atom child, String name){
			this(child);
			this.name = name;
		}
	}

	public Object child;

	protected void configure(Configuration conf) {
		conf.objectClass(this).cascadeOnUpdate(true);
	}

	protected void store() {
		CascadeOnUpdate cou = new CascadeOnUpdate();
		cou.child = new Atom(new Atom("storedChild"), "stored");
		db().set(cou);
	}

	public void test() throws Exception {
		foreach(getClass(), new Visitor4() {
			public void visit(Object obj) {
				CascadeOnUpdate cou = (CascadeOnUpdate) obj;
				((Atom)cou.child).name = "updated";
				((Atom)cou.child).child.name = "updated";
				db().set(cou);
			}
		});
		
		reopen();
		
		foreach(getClass(), new Visitor4() {
			public void visit(Object obj) {
				CascadeOnUpdate cou = (CascadeOnUpdate) obj;
				Atom atom = (Atom)cou.child;
				Assert.areEqual("updated", atom.name);
				Assert.areNotEqual("updated", atom.child.name);
			}
		});
	}
}
