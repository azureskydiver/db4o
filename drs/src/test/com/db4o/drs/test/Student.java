/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;


public class Student extends Person {

	private String _studentno;
	
	public Student(String name, int age) {
		super(name, age);
	}
	
	public void setStudentNo(String studentno) {
		this._studentno = studentno;
	}
	
	public String getStudentNo() {
		return _studentno;
	}
}
