/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.foundation;

public interface Queue4 {

	void add(Object obj);

	Object next();

	boolean hasNext();
	
	/**
	 * Returns the next object in the queue that matches the specified condition.
	 * 
	 * The operation is always NON-BLOCKING.
	 *  
	 * @param predicate condition the object must satisfy to be returned
	 * 
	 * @return the object satisfying the condition or null if none does
	 */
	Object nextMatching(Predicate4 condition);

	Iterator4 iterator();
}