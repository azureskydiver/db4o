/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/** Deep clone */
public interface DeepClone {

	/** The parameter allows passing one new object so parent
	  * references can be corrected on children.*/
	Object deepClone(Object obj) throws CloneNotSupportedException;

}
