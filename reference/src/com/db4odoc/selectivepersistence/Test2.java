/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.selectivepersistence;

public class Test2 {
	private Test1 test1;

	private String name;

	private NotStorable transientClass;

	public Test2(String name, NotStorable transientClass, Test1 test1) {
		this.test1 = test1;
		this.name = name;
		this.transientClass = transientClass;
	}

	public String toString() {
		return name + "/" + transientClass + "; test1: " + test1;
	}
}
