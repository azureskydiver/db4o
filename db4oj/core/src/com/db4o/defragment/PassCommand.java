/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import java.io.IOException;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;

/**
 * Implements one step in the defragmenting process.
 * 
 * @exclude
 */
interface PassCommand {
	void processObjectSlot(DefragContextImpl context,ClassMetadata yapClass,int id, boolean registerAddresses) throws CorruptionException, IOException;
	void processClass(DefragContextImpl context,ClassMetadata yapClass,int id,int classIndexID) throws CorruptionException, IOException;
	void processClassCollection(DefragContextImpl context) throws CorruptionException, IOException;
	void processBTree(DefragContextImpl context, BTree btree) throws CorruptionException, IOException;
	void flush(DefragContextImpl context);
}
