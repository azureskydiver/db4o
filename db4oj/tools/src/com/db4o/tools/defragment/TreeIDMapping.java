/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import com.db4o.*;
import com.db4o.foundation.*;


/**
 * @exclude
 */
public class TreeIDMapping extends AbstractIDMapping {
	
	private Tree _tree;
	
	public void close() {
	}

	public void mapIDs(int oldID, int newID) {
		_tree = Tree.add(_tree, new TreeIntObject(oldID, new Integer(newID)));
	}

	public Integer mappedID(int oldID, boolean lenient) {
		Integer classID=mappedClassID(oldID);
		if(classID!=null) {
			return classID;
		}
		TreeIntObject res = (TreeIntObject) TreeInt.find(_tree, oldID);
		if(res != null){
			return (Integer)res._object;
		}
		if(lenient){
			TreeIntObject nextSmaller = (TreeIntObject) Tree.findSmaller(_tree, new TreeInt(oldID));
			if(nextSmaller != null){
				int baseOldID = nextSmaller._key;
				int baseNewID = ((Integer)nextSmaller._object).intValue();
				return new Integer(baseNewID + oldID - baseOldID); 
			}
		}
		return null;
	}
	

}
