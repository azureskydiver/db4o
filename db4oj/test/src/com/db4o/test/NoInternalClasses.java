/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;

public class NoInternalClasses {
	
	public void store(){
		Test.store(new StaticClass());
	}
	
	public void test(){
		Test.ensureOccurrences(new StaticClass(),0);
	}

}
