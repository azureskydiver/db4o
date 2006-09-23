/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.refactoring.newclasses;


public class Identity {
	private String  name;
	private String id;
	
	public Identity(String name, String id){
		this.name = name;
		this.id = id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String toString() {
        return name + "["+id+"]";
    }
	
}
