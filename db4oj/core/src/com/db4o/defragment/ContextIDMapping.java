/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

/**
 * The ID mapping used internally during a defragmentation run.
 * 
 * @see Defragment
 */
interface ContextIDMapping {

	int mappedID(int oldID, boolean lenient);

	void mapIDs(int oldID, int newID);

	void open();
	
	void close();

	void mapClassIDs(int oldID, int newID);

}