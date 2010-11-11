/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.vod.example.data;

public class Customer {
	
	private String _name;
	
	public Customer(String name) {
		_name = name;
	}

	public Customer(){
		
	}
	
	@Override
	public String toString() {
		return "Customer " + _name;
	}

}
