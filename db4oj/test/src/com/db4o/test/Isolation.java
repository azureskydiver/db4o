/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;

public class Isolation {
	
	public void store(){
		Test.deleteAllInstances(this);
	}
	
	public void test(){
		if(Test.isClientServer()){
			ObjectContainer oc = Test.currentServer().ext().objectContainer();
			oc.set(new Isolation());
			Test.ensure(Test.occurrences(this) == 0);
			oc.commit();
			Test.ensure(Test.occurrences(this) == 1);
		}
	}

}
