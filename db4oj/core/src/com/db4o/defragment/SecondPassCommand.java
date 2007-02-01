/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.btree.*;

/**
 * Second step in the defragmenting process: Fills in target file pointer slots, copies
 * content slots from source to target and triggers ID remapping therein by calling the
 * appropriate yap/marshaller defrag() implementations. During the process, the actual address
 * mappings for the content slots are registered for use with string indices.
 * 
 * @exclude
 */
final class SecondPassCommand implements PassCommand {

	private final int _objectCommitFrequency;
	private int _objectCount=0;
	
	
	
	public SecondPassCommand(int objectCommitFrequency) {
		_objectCommitFrequency = objectCommitFrequency;
	}

	public void processClass(final DefragContextImpl context, final YapClass yapClass, int id,final int classIndexID) throws CorruptionException {
		if(context.mappedID(id,-1)==-1) {
			System.err.println("MAPPING NOT FOUND: "+id);
		}
		ReaderPair.processCopy(context, id, new SlotCopyHandler() {
			public void processCopy(ReaderPair readers) throws CorruptionException {
				yapClass.defragClass(readers, classIndexID);
			}
		});
	}

	public void processObjectSlot(final DefragContextImpl context, final YapClass yapClass, int id, boolean registerAddresses) throws CorruptionException {
		ReaderPair.processCopy(context, id, new SlotCopyHandler() {
			public void processCopy(ReaderPair readers) {
				YapClass.defragObject(readers);
				if(_objectCommitFrequency>0) {
					_objectCount++;
					if(_objectCount==_objectCommitFrequency) {
						context.targetCommit();
						_objectCount=0;
					}
				}
			}
		},registerAddresses);
	}

	public void processClassCollection(DefragContextImpl context) throws CorruptionException {
		ReaderPair.processCopy(context, context.sourceClassCollectionID(), new SlotCopyHandler() {
			public void processCopy(ReaderPair readers) {
				YapClassCollection.defrag(readers);
			}
		});
	}

	public void processBTree(final DefragContextImpl context, BTree btree) throws CorruptionException {
		btree.defragBTree(context);
	}

	public void flush(DefragContextImpl context) {
	}
}