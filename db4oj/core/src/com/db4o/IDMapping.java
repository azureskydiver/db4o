/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;


/**
 * @exclude
 */
public interface IDMapping {
	/**
	 * @return a mapping for the given id if it doesn't refer to a system handler and a mapping is known, the given id otherwise
	 */
	int mappedID(int oldID);
}
