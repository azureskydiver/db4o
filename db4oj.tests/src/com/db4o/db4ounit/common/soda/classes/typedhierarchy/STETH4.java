/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.soda.classes.typedhierarchy;

public class STETH4 extends STETH2{
	
	public String foo4;
	
	public STETH4(){
	}
	
	public STETH4(String str1, String str2, String str3){
		super(str1, str2);
		foo4 = str3;
	}
	
}

