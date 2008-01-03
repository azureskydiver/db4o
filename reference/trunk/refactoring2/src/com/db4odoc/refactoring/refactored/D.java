/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4odoc.refactoring.refactored;

import java.util.Date;

import com.db4odoc.refactoring.initial.B;

public class D extends B {
	public Date storedDate;

	public String toString(){
		return name + "/" + number + ": " + storedDate;
	}
}