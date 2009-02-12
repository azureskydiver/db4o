/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

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
	
	private final LocalTransaction _localTransaction;

	private final int _id;
	
	private final ClassMetadata _clazz;
	
	private final int _typeInfo;
	
	private int _cascade;

	public WriteUpdateProcessor(LocalTransaction localTransaction, 
			int id, 
			ClassMetadata clazz, 
			int typeInfo,
			int cascade) {
		_localTransaction = localTransaction;
		_id = id;
		_clazz = clazz;
		_typeInfo = typeInfo;
		_cascade = cascade;
	}

	public void run(){
    	_localTransaction.checkSynchronization();
    	
        if(DTrace.enabled){
            DTrace.WRITE_UPDATE_ADJUST_INDEXES.log(_id);
        }
        
        if(alreadyHandled()){
        	return;
        }
        
        if(handledWithNoChildIndexModification()){
        	return;
        }
        
        StatefulBuffer objectBytes = _localTransaction.container().readWriterByID(localTransaction(), _id);
        if(handledAsReAdd(objectBytes)){
        	return;
        }
        
        updateChildIndexes(objectBytes);
        
        freeSlotOnCommit(objectBytes);
	}

	private void freeSlotOnCommit(StatefulBuffer objectBytes) {
		_localTransaction.slotFreeOnCommit(_id, new Slot(objectBytes.getAddress(), objectBytes.length()));
	}

	private void updateChildIndexes(StatefulBuffer objectBytes) {
		ObjectHeader oh = new ObjectHeader(_localTransaction.container(), _clazz, objectBytes);
        
        DeleteInfo info = (DeleteInfo)TreeInt.find(_localTransaction._delete, _id);
        if(info != null){
            if(info._cascade > _cascade){
                _cascade = info._cascade;
            }
        }
        
        objectBytes.setCascadeDeletes(_cascade);
        
        DeleteContextImpl context = new DeleteContextImpl(objectBytes, oh, _clazz.classReflector(), null);
        _clazz.deleteMembers(context, _typeInfo, true);
	}

	private boolean handledAsReAdd(StatefulBuffer objectBytes) {
		if(objectBytes != null){
			return false;
		}
        _clazz.addToIndex(localTransaction(), _id);
        return true;
	}
	
	private boolean alreadyHandled() {
		TreeInt newNode = new TreeInt(_id);
        _localTransaction._writtenUpdateAdjustedIndexes = Tree.add(_localTransaction._writtenUpdateAdjustedIndexes, newNode);
        return ! newNode.wasAddedToTree();
	}
	
	private boolean handledWithNoChildIndexModification() {
		if(! _clazz.canUpdateFast()){
			return false;
		}
    	Slot currentSlot = _localTransaction.getCurrentSlotOfID(_id);
    	if(currentSlot == null || currentSlot.address() == 0){
    	    _clazz.addToIndex(localTransaction(), _id);
    	}else{
    	    _localTransaction.slotFreeOnCommit(_id, currentSlot);
    	}
    	return true;
	}

	private LocalTransaction localTransaction() {
		return _localTransaction;
	}

}