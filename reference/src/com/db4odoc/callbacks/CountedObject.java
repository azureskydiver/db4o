/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
/*
 * This class is used to mark classes that need to get an autoincremented ID
 */
package com.db4odoc.callbacks;


public abstract class CountedObject {
	int id;
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
}
