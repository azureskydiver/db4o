/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.legacy.soda.classes.typedhierarchy;

public class STTH2 {
	
	public STTH3 h3;
	public String foo2;
	
	public STTH2(){
	}
	
	public STTH2(STTH3 a3){
		h3 = a3;
	}
	
	public STTH2(String str){
		foo2 = str;
	}
	
	public STTH2(STTH3 a3, String str){
		h3 = a3;
		foo2 = str;
	}
	
}

