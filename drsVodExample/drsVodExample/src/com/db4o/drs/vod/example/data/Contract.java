/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.vod.example.data;

public class Contract {
	
	private String _name;
	
	private Customer _customer;
	
	private int _amount;
	
	public Contract(){
		
	}
	
	public Contract(String name, Customer customer, int amount) {
		_name = name;
		_customer = customer;
		_amount = amount;
	}
	
	@Override
	public String toString() {
		return "Contract [_amount=" + _amount + ", _customer=" + _customer
		+ ", _name=" + _name + "]";
	}
	

}
