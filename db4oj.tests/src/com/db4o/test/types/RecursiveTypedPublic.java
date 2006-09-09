/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.types;

public class RecursiveTypedPublic extends RTest
{
	public RecursiveTypedPublic recurse;
	public String depth;
	
	public void set(int ver){
		set(ver,10);
	}
	
	private void set(int ver, int a_depth){
		depth = "s" + ver + ":" +  a_depth;	
		if(a_depth > 0){
			recurse = new RecursiveTypedPublic();
			recurse.set(ver, a_depth - 1);
		}
	}

}
