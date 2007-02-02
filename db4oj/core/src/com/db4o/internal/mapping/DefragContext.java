/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.mapping;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;

/**
 * Encapsulates services involving source and target database files during defragmenting.
 * 
 * @exclude
 */
public interface DefragContext extends IDMapping {
	
	Buffer sourceReaderByAddress(int address,int length);
	Buffer targetReaderByAddress(int address,int length);

	Buffer sourceReaderByID(int sourceID);

	int allocateTargetSlot(int targetLength);

	void targetWriteBytes(Buffer targetPointerReader, int targetID);

	Transaction systemTrans();

	void targetWriteBytes(ReaderPair readers, int targetAddress);

	void traverseAllIndexSlots(BTree tree, Visitor4 visitor4);	
	
	ClassMetadata yapClass(int id);

	StatefulBuffer sourceWriterByID(int sourceID);
	
	int mappedID(int id,boolean lenient);

	void registerUnindexed(int id);
	
	Iterator4 unindexedIDs();
}