/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import com.db4o.foundation.*;


/**
 * @exclude
 */
abstract class AbstractIDMapping implements DefragmentContextIDMapping {

	private Hashtable4	_classIDs = new Hashtable4();

	protected Integer mappedClassID(int origID) {
		return (Integer)_classIDs.get(origID);
	}

	public void mapClassIDs(int oldID, int newID) {
		_classIDs.put(oldID,new Integer(newID));
	}

}
