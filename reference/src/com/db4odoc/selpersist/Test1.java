/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.selpersist;


public class Test1 {
	private String name;
	private NotStorable transientClass;
	
	public Test1(String name, NotStorable transientClass){
		this.name = name;
		this.transientClass = transientClass;
	}
	
	public String toString(){
		return name + "/" + transientClass;
	}
}
