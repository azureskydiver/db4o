/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

abstract class Config4Abstract
{
	int i_cascadeOnActivate = 0;
	int i_cascadeOnDelete = 0;
	int i_cascadeOnUpdate = 0;
	String i_name;
	
	public void cascadeOnActivate(boolean flag){
		i_cascadeOnActivate = flag ? 1 : -1;
	}
	
	public void cascadeOnDelete(boolean flag){
		i_cascadeOnDelete = flag ? 1 : -1;
	}
	
	public void cascadeOnUpdate(boolean flag){
		i_cascadeOnUpdate = flag ? 1 : -1;
	}

	abstract String className();
	
	public boolean equals(Object obj){
		return i_name.equals(((Config4Abstract)obj).i_name);
	}

	public String getName(){
		return i_name;
	}
	
}
