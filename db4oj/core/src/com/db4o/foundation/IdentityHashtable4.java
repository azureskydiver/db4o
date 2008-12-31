/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class IdentityHashtable4 extends HashtableBase{
	
	public IdentityHashtable4(){
	}
	
	public IdentityHashtable4(int size){
		super(size);
	}
	
	public boolean contains(Object obj){
		return findWithSameKey(new HashtableIdentityEntry(obj)) != null;
	}
	
	public void put(Object obj){
		if(null == obj){
			throw new ArgumentNullException();
		}
		putEntry(new HashtableIdentityEntry(obj));
	}

}
