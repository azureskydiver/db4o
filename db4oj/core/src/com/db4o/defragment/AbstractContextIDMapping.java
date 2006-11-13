/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import com.db4o.foundation.*;


/**
 * Base class for defragment ID mappings.
 * 
 * @see Defragment
 */
abstract class AbstractContextIDMapping implements ContextIDMapping {

	private Hashtable4	_classIDs = new Hashtable4();

	public final void mapIDs(int origID, int mappedID, boolean isClassID) {
		if(isClassID) {
			mapClassIDs(origID, mappedID);
			return;
		}
		mapNonClassIDs(origID, mappedID);
	}

	protected int mappedClassID(int origID) {
		Object obj = _classIDs.get(origID);
		if(obj == null){
			return 0;
		}
		return ((Integer)obj).intValue();
	}

	private void mapClassIDs(int oldID, int newID) {
		_classIDs.put(oldID,new Integer(newID));
	}

	protected abstract void mapNonClassIDs(int origID,int mappedID);
}
