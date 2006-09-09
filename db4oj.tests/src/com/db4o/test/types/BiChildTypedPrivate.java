/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.types;

public class BiChildTypedPrivate
{
	private BiParentTypedPrivate parent;
	private String name;
	
	public BiChildTypedPrivate(){
	}
	
	public BiChildTypedPrivate(BiParentTypedPrivate a_parent, String a_name){
		parent = a_parent;
		name = a_name;
	}
}
