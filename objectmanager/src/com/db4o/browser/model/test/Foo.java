package com.db4o.browser.model.test;

public class Foo {
	private String s;
	
	public Foo(String s) {
		this.s = s;
	}

	public String s() {
		return s;
	}
	
	public void s(String s) {
		this.s = s;
	}
}
