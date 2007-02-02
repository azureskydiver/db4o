/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;


/**
 * In-memory mapping for IDs during a defragmentation run.
 * 
 * @see Defragment
 */
public class TreeIDMapping extends AbstractContextIDMapping {
	
	private Tree _tree;
	
	public int mappedID(int oldID, boolean lenient) {
		int classID = mappedClassID(oldID);
		if(classID != 0) {
			return classID;
		}
		TreeIntObject res = (TreeIntObject) TreeInt.find(_tree, oldID);
		if(res != null){
			return ((Integer)res._object).intValue();
		}
		if(lenient){
			TreeIntObject nextSmaller = (TreeIntObject) Tree.findSmaller(_tree, new TreeInt(oldID));
			if(nextSmaller != null){
				int baseOldID = nextSmaller._key;
				int baseNewID = ((Integer)nextSmaller._object).intValue();
				return baseNewID + oldID - baseOldID; 
			}
		}
		return 0;
	}

	public void open() {
	}
	
	public void close() {
	}

	protected void mapNonClassIDs(int origID, int mappedID) {
		_tree = Tree.add(_tree, new TreeIntObject(origID, new Integer(mappedID)));
	}
}
