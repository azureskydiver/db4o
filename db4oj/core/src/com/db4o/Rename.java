/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

public final class Rename implements Internal
{
	public String rClass;
	public String rFrom;
	public String rTo;
	
	public Rename(){}
	
	public Rename(String aClass, String aFrom, String aTo){
		rClass = aClass;
		rFrom = aFrom;
		rTo = aTo;
	}
}
