package com.db4o.browser.model.test;

public class Bar extends Foo {
	private int[] i;

	public Bar(String s, int[] i) {
		super(s);
		this.i=i;
	}

	public int[] i() {
		return i;
	}
}
