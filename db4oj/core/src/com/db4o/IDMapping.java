/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;


/**
 * @exclude
 */
public interface IDMapping {
	/**
	 * @return a mapping for the given id if it doesn't refer to a system handler, the given id otherwise
	 * @throws MappingNotFoundException if the given id does not refer to a system handler and if no mapping is found
	 */
	int mappedID(int oldID) throws MappingNotFoundException;
	int mappedID(int oldID,int defaultID);
	void mapIDs(int oldID,int newID);
}
