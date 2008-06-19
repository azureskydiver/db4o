package com.db4o.ibs.tests;

public class Contact {

	private String _email;

	public Contact(String email) {
		_email = email;
	}
	
	public String email() {
		return _email;
	}

}
