/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.btree.*;

/**
 * @exclude
 */
public interface DefragContext extends IDMapping {
	YapReader sourceReaderByID(int sourceID);

	int allocateTargetSlot(int targetLength);

	void targetWriteBytes(YapReader targetPointerReader, int targetID);

	Transaction systemTrans();

	void targetWriteBytes(ReaderPair readers, int targetAddress);

	void traverseAllIndexSlots(BTree tree, Visitor4 visitor4);	
}