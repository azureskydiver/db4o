/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;

public class CascadeOnActivate {
	
	public String name;
	public CascadeOnActivate child;
	
	public void configure(){
		Db4o.configure().objectClass(this).cascadeOnActivate(true);
	}
	
	public void store(){
		name = "1";
		child = new CascadeOnActivate();
		child.name = "2";
		child.child = new CascadeOnActivate();
		child.child.name = "3";
		Test.store(this);
	}
	
	public void test(){
		Query q = Test.query();
		q.constrain(this.getClass());
		q.descend("name").constrain("1");
		ObjectSet os = q.execute();
		CascadeOnActivate coa = (CascadeOnActivate)os.next();
		CascadeOnActivate coa3 = coa.child.child;
		Test.ensure(coa3.name.equals("3"));
		Test.objectContainer().deactivate(coa, Integer.MAX_VALUE);
		Test.ensure(coa3.name == null);
		Test.objectContainer().activate(coa, 1);
		Test.ensure(coa3.name.equals("3"));
	}

}
