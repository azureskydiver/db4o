/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.slots.*;
		
/**
 * First step in the defragmenting process: Allocates pointer slots in the target file for
 * each ID (but doesn't fill them in, yet) and registers the mapping from source pointer address
 * to target pointer address.
 * 
 * @exclude
 */
final class FirstPassCommand implements PassCommand {
	private final static int ID_BATCH_SIZE=4096;

	private TreeInt _ids;
	
	void process(DefragmentServicesImpl context, int objectID, boolean isClassID) {
		if(batchFull()) {
			flush(context);
		}
		_ids=TreeInt.add(_ids,(isClassID ? -objectID : objectID));
	}

	private boolean batchFull() {
		return _ids!=null&&_ids.size()==ID_BATCH_SIZE;
	}

	public void processClass(DefragmentServicesImpl context, ClassMetadata yapClass,int id,int classIndexID) {
		process(context,id, true);
		for (int fieldIdx = 0; fieldIdx < yapClass.i_fields.length; fieldIdx++) {
			FieldMetadata field=yapClass.i_fields[fieldIdx];
			if(!field.isVirtual()&&field.hasIndex()) {
				processBTree(context,field.getIndex(context.systemTrans()));
			}
		}
	}

	public void processObjectSlot(DefragmentServicesImpl context, ClassMetadata yapClass, int sourceID) {
		process(context,sourceID, false);
	}

	public void processClassCollection(DefragmentServicesImpl context) throws CorruptionException {
		process(context,context.sourceClassCollectionID(), false);
	}

	public void processBTree(final DefragmentServicesImpl context, final BTree btree) {
		process(context,btree.getID(), false);
		context.traverseAllIndexSlots(btree, new Visitor4() {
			public void visit(Object obj) {
				int id=((Integer)obj).intValue();
				process(context,id, false);
			}
		});
	}

	public void flush(DefragmentServicesImpl context) {
		if(_ids==null) {
			return;
		}
		int blockSize = context.blockSize();
		boolean overlapping=(Const4.POINTER_LENGTH%blockSize>0);
		int blocksPerPointer=Const4.POINTER_LENGTH/blockSize;
		if(overlapping) {
			blocksPerPointer++;
		}
		int bytesPerPointer = blocksPerPointer * blockSize;
		int batchSize = _ids.size() * bytesPerPointer;
		Slot pointerSlot = context.allocateTargetSlot(batchSize);
		int pointerAddress=pointerSlot.address();
		Iterator4 idIter=new TreeKeyIterator(_ids);
		while(idIter.moveNext()) {
			int objectID=((Integer)idIter.current()).intValue();
			boolean isClassID=false;
			if(objectID<0) {
				objectID=-objectID;
				isClassID=true;
			}
			
			if(DefragmentConfig.DEBUG){
				int mappedID = context.mappedID(objectID, -1);
				// seen object ids don't come by here anymore - any other candidates?
				if(mappedID>=0) {
					throw new IllegalStateException();
				}
			}
			
			context.mapIDs(objectID,pointerAddress, isClassID);
			pointerAddress+=blocksPerPointer;
		}
		_ids=null;
	}
}