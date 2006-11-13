/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import com.db4o.foundation.*;


/**
 * Base class for defragment ID mappings.
 */
abstract class AbstractContextIDMapping implements ContextIDMapping {

	private Hashtable4	_classIDs = new Hashtable4();

	protected int mappedClassID(int origID) {
		Object obj = _classIDs.get(origID);
		if(obj == null){
			return 0;
		}
		return ((Integer)obj).intValue();
	}

	public void mapClassIDs(int oldID, int newID) {
		_classIDs.put(oldID,new Integer(newID));
	}

}
