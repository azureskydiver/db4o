/* Copyright (C) 2004 - 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.slots;

import com.db4o.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class SlotChange extends TreeInt {
	
	private static class SlotChangeOperation {
		
		private final String _type;
		
		public SlotChangeOperation(String type) {
			_type = type;
		}

		static final SlotChangeOperation create = new SlotChangeOperation("create");
		
		static final SlotChangeOperation update = new SlotChangeOperation("update");
		
		static final SlotChangeOperation delete = new SlotChangeOperation("delete");
		
		@Override
		public String toString() {
			return _type;
		}
		
	}
	
	private SlotChangeOperation _firstOperation;
	
	private SlotChangeOperation _currentOperation;
	
	private Slot _newSlot;
    
	public SlotChange(int id) {
		super(id);
	}
	
	public Object shallowClone() {
		SlotChange sc = new SlotChange(0);
		sc.newSlot(_newSlot);
		return super.shallowCloneInternal(sc);
	}

	public void freeDuringCommit(LocalObjectContainer file, boolean forFreespace) {
        if( isForFreespace() != forFreespace){
        	return;
        }
    	if(_firstOperation == SlotChangeOperation.create){
    		return;
    	}
		if(_currentOperation == SlotChangeOperation.update || _currentOperation == SlotChangeOperation.delete){
			Slot slot = file.idSystem().getCommittedSlotOfID(_key);
			
			// If we don't get a valid slot, the object may have just 
			// been stored by the SystemTransaction and not committed yet.
			if(slot == null || slot.isNull()){
				slot = findCurrentSlotInSystemTransaction(file); 
			}
			
			// No old slot at all can be the case if the object
			// has been deleted by another transaction and we add it again.
			if(slot != null && ! slot.isNull()){
				file.free(slot);
			}
		}
    	
	}

	protected Slot findCurrentSlotInSystemTransaction(LocalObjectContainer file) {
		return file.idSystem().getCurrentSlotOfID((LocalTransaction)file.systemTransaction(), _key);
	}
	
	public boolean isDeleted() {
		return slotModified() && _newSlot.isNull();
	}
	
	public boolean isNew() {
		return isFreePointerOnRollback();
	}
    
	private final boolean isFreeOnRollback() {
		return _newSlot != null && ! _newSlot.isNull();
	}

	public final boolean slotModified() {
		return _newSlot != null;
	}
	
	/**
	 * FIXME:	Check where pointers should be freed on commit.
	 * 			This should be triggered in this class.
	 */
//	private final boolean isFreePointerOnCommit() {
//		return isBitSet(FREE_POINTER_ON_COMMIT_BIT);
//	}

	public final boolean isFreePointerOnRollback() {
		return _firstOperation == SlotChangeOperation.create;
	}

	public Slot newSlot() {
		return _newSlot;
	}
    
	public Object read(ByteArrayBuffer reader) {
		SlotChange change = new SlotChange(reader.readInt());
		Slot newSlot = new Slot(reader.readInt(), reader.readInt());
		change.newSlot(newSlot);
		return change;
	}

	public void rollback(LocalObjectContainer container) {
		if (isFreeOnRollback()) {
			container.free(_newSlot);
		}
		if(isFreePointerOnRollback()){
		    if(DTrace.enabled){
		        DTrace.FREE_POINTER_ON_ROLLBACK.logLength(_key, Const4.POINTER_LENGTH);
		    }
			container.free(_key, Const4.POINTER_LENGTH);
		}
	}

	public void write(ByteArrayBuffer writer) {
		if (slotModified()) {
			writer.writeInt(_key);
			writer.writeInt(_newSlot.address());
			writer.writeInt(_newSlot.length());
		} 
	}

	public final void writePointer(LocalObjectContainer container) {
		if (slotModified()) {
			container.writePointer(_key, _newSlot);
		}
	}
    
    private void newSlot(Slot slot){
    	_newSlot = slot;
    }

	public void notifySlotUpdated(LocalObjectContainer file, Slot slot) {
		if(DTrace.enabled){
			DTrace.NOTIFY_SLOT_CHANGED.log(_key);
			DTrace.NOTIFY_SLOT_CHANGED.logLength(slot);
		}
		freePreviouslyModifiedSlot(file);
		_newSlot = slot;
		operation(SlotChangeOperation.update);
	}

	protected void freePreviouslyModifiedSlot(LocalObjectContainer file) {
		if(_newSlot == null ){
			return;
		}
		if(_newSlot.isNull()){
			return;
		}
		free(file, _newSlot);
		_newSlot = null;
	}

	protected void free(LocalObjectContainer file, Slot slot) {
		if(slot.isNull()){
			return;
		}
		file.free(slot);
	}

	private void operation(SlotChangeOperation operation) {
		if(_firstOperation == null){
			_firstOperation = operation;
		}
		_currentOperation = operation;
	}

	public void notifySlotCreated(Slot slot) {
		if(DTrace.enabled){
			DTrace.NOTIFY_SLOT_CREATED.log(_key);
			DTrace.NOTIFY_SLOT_CREATED.logLength(slot);
		}
		operation(SlotChangeOperation.create);
		_newSlot = slot;
	}

	public void notifyDeleted(LocalObjectContainer file) {
		if(DTrace.enabled){
			DTrace.NOTIFY_SLOT_DELETED.log(_key);
		}
		operation(SlotChangeOperation.delete);
		freePreviouslyModifiedSlot(file);
		_newSlot = Slot.ZERO;
	}
	
	protected boolean isForFreespace(){
		return false;
	}
    
}
