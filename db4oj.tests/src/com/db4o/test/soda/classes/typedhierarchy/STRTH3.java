/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.soda.classes.typedhierarchy;

public class STRTH3{
	
	STRTH1 grandParent;
	STRTH2 parent;
	
	String foo3;
	
	public STRTH3(){
	}
	
	public STRTH3(String str){
		foo3 = str;
	}
}

