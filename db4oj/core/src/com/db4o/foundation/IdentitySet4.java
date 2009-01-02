/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class IdentitySet4 extends HashtableBase{
	
	public IdentitySet4(){
	}
	
	public IdentitySet4(int size){
		super(size);
	}
	
	public boolean contains(Object obj){
		return findWithSameKey(new HashtableIdentityEntry(obj)) != null;
	}
	
	public void add(Object obj){
		if(null == obj){
			throw new ArgumentNullException();
		}
		putEntry(new HashtableIdentityEntry(obj));
	}

}
