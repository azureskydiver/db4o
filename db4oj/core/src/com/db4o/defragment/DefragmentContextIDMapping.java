/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

/**
 * @exclude
 */
interface DefragmentContextIDMapping {

	Integer mappedID(int oldID, boolean lenient);

	void mapIDs(int oldID, int newID);

	void close();

	void mapClassIDs(int oldID, int newID);

}