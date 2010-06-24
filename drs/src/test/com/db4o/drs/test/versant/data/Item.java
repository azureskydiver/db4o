/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant.data;

public class Item {
	
	private String _name;
	
	public Item(){
		
	}
	
	public Item(String name){
		_name = name;
	}
	
	public String name(){
		return _name;
	}
	

}
