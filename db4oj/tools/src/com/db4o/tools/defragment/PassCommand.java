/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import com.db4o.*;
import com.db4o.inside.btree.*;

/**
 * Implements one step in the defragmenting process.
 * 
 * @exclude
 */
interface PassCommand {
	void processObjectSlot(DefragContextImpl context,YapClass yapClass,int id, boolean registerAddresses) throws CorruptionException;
	void processClass(DefragContextImpl context,YapClass yapClass,int id,int classIndexID) throws CorruptionException;
	void processClassCollection(DefragContextImpl context) throws CorruptionException;
	void processBTree(DefragContextImpl context, BTree btree) throws CorruptionException;
	void flush(DefragContextImpl context);
}
