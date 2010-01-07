/* Copyright (C) 2009  Versant Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
class WriteUpdateProcessor {
	
	private final LocalTransaction _transaction;

	private final int _id;
	
	private final ClassMetadata _clazz;
	
	private final ArrayType _typeInfo;
	
	private int _cascade;

	public WriteUpdateProcessor(LocalTransaction transaction, 
			int id, 
			ClassMetadata clazz, 
			ArrayType typeInfo,
			int cascade) {
		_transaction = transaction;
		_id = id;
		_clazz = clazz;
		_typeInfo = typeInfo;
		_cascade = cascade;
	}

	public void run(){
    	_transaction.checkSynchronization();
    	
        if(DTrace.enabled){
            DTrace.WRITE_UPDATE_ADJUST_INDEXES.log(_id);
        }
        
        if(alreadyHandled()){
        	return;
        }
        
        Slot slot = container().idSystem().getCurrentSlotOfID(_transaction, _id);
        if(handledAsReAdd(slot)){
        	return;
        }
        
        if(handledWithNoChildIndexModification(slot)){
        	return;
        }
        
        StatefulBuffer objectBytes = (StatefulBuffer)container().readReaderOrWriterBySlot(_transaction, _id, false, slot);
        
        updateChildIndexes(objectBytes);
        
        freeSlotOnCommit(objectBytes);
	}

	private LocalObjectContainer container() {
		return _transaction.localContainer();
	}

	private void freeSlotOnCommit(StatefulBuffer objectBytes) {
		container().idSystem().slotFreeOnCommit(_transaction, _id, objectBytes.slot());
	}

	private void updateChildIndexes(StatefulBuffer objectBytes) {
		ObjectHeader oh = new ObjectHeader(container(), _clazz, objectBytes);
        
        DeleteInfo info = (DeleteInfo)TreeInt.find(_transaction._delete, _id);
        if(info != null){
            if(info._cascade > _cascade){
                _cascade = info._cascade;
            }
        }
        
        objectBytes.setCascadeDeletes(_cascade);
        
        DeleteContextImpl context = new DeleteContextImpl(objectBytes, oh, _clazz.classReflector(), null);
        _clazz.deleteMembers(context, _typeInfo, true);
	}

	private boolean handledAsReAdd(Slot slot) {
		if(slot != null  && !slot.isNull()){
			return false;
		}
        _clazz.addToIndex(_transaction, _id);
        return true;
	}
	
	private boolean alreadyHandled() {
		TreeInt newNode = new TreeInt(_id);
        _transaction._writtenUpdateAdjustedIndexes = Tree.add(_transaction._writtenUpdateAdjustedIndexes, newNode);
        return ! newNode.wasAddedToTree();
	}
	
	private boolean handledWithNoChildIndexModification(Slot slot) {
		if(! _clazz.canUpdateFast()){
			return false;
		}
		container().idSystem().slotFreeOnCommit(_transaction, _id, slot);
    	return true;
	}

}