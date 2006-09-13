/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.classindex;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public interface ClassIndexStrategy {	
	void initialize(YapStream stream);
	void read(YapStream stream, int indexID);
	int write(Transaction transaction);
	void add(Transaction transaction, int id);
	void remove(Transaction transaction, int id);
	int entryCount(Transaction transaction);
	int ownLength();
	void purge();
	
	/**
	 * Traverses all index entries (java.lang.Integer references).
	 */
	void traverseAll(Transaction transaction,Visitor4 command);
	void dontDelete(Transaction transaction, int id);
	
	Iterator4 allSlotIDs(Transaction trans);
	void defragReference(YapClass yapClass,YapReader source,YapReader target,IDMapping mapping,int classIndexID);
	int id();
	void defragIndex(YapReader source, YapReader target, IDMapping mapping);
}
